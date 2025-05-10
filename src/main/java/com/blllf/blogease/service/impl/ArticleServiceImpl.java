package com.blllf.blogease.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blllf.blogease.mapper.ArticleMapper;
import com.blllf.blogease.mapper.MessageMapper;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.*;
import com.blllf.blogease.pojo.dto.*;
import com.blllf.blogease.service.ArticleService;
import com.blllf.blogease.service.RedisService;
import com.blllf.blogease.util.HandleResponseUtil;
import com.blllf.blogease.util.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper , Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RedisService redisService;

    //admin 新增文章


    @Override
    public void adminAdd(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setDeleted(0);
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        article.setCreateUser(uid);
        articleMapper.adminAdd(article);
    }

    //普通用户新增文章
    @Override
    public void add(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        //提交的文章需要管理员审核
        if ("发布中".equals(article.getState())){
            article.setDeleted(1);
        }else {
            //草稿
            article.setDeleted(2);
        }
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        article.setCreateUser(uid);
        articleMapper.add(article);
    }
    /**
     * 使用PageHelper 插件进行分页
     * */
    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, Integer deleted) {
        PageBean<Article> pb = new PageBean<>();

        PageHelper.startPage(pageNum,pageSize);

        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        Page<Article> articles = articleMapper.list(uid,categoryId , deleted);

        pb.setTotal(articles.getTotal());
        pb.setItems(articles.getResult());

        return pb;
    }

    @Override
    public PageBean<Article> selectAll(Integer pageNum, Integer pageSize, Integer categoryId, String title , String author , String testDate1, String testDate2) {

        if (author != null && !author.isEmpty()){
            author = "%" + author + "%";
        }

        //查询数据库表user 根据模糊查询 得到ID
        List<Integer> people = userMapper.selectUserIdsByUsername(author);

        PageBean<Article> pb = new PageBean<>();
        //PageHelper.startPage 必须在执行查询之前调用，而且只能影响紧跟着它的第一个 SQL 查询。
        PageHelper.startPage(pageNum,pageSize);
        //文章状态只能是已发布
        String state = "已发布";
        if (title != null && !title.isEmpty()){
            title = "%" + title + "%";
        }

        String str1 = null;
        String str2 = null;

        if ((testDate1 != null && !testDate1.isEmpty()) && (testDate2 != null && !testDate2.isEmpty())){
            //对testDate1 testDate2 修整
            // 定义一个DateTimeFormatter来解析包含毫秒和Z的ISO 8601日期时间字符串
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            // 解析字符串为OffsetDateTime对象
            OffsetDateTime dateTime = OffsetDateTime.parse(testDate1, parser);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(testDate2, parser);
            // 定义一个DateTimeFormatter来格式化日期时间字符串，不包含毫秒和Z
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            // 格式化OffsetDateTime对象为字符串
            String newDate1 = dateTime.format(formatter);
            String newDate2 = dateTime2.format(formatter);
            //由于ISO格式的时间比GMT格式的时间慢8个小时，队获得的时间做个调整
            LocalDateTime date1 = LocalDateTime.parse(newDate1, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime date2 = LocalDateTime.parse(newDate2, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime localDateTime1 = date1.plusHours(8);
            LocalDateTime localDateTime2 = date2.plusHours(8);
            str1 = localDateTime1.toString();
            str2 = localDateTime2.toString();
        }

        Page<Article> articles = articleMapper.selectAll(categoryId, state, people, title , str1 , str2);

        pb.setTotal(articles.getTotal());
        pb.setItems(articles.getResult());

        return pb;
    }

    @Override
    public List<Article> selectArticlesAudit(Integer categoryId, String date1, String date2) {
        String str1 = null;
        String str2 = null;
        if ((date1 != null && !date1.isEmpty()) && (date2 != null && !date2.isEmpty())){
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            OffsetDateTime dateTime = OffsetDateTime.parse(date1, parser);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(date2, parser);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String newDate1 = dateTime.format(formatter);
            String newDate2 = dateTime2.format(formatter);
            LocalDateTime nowDate1 = LocalDateTime.parse(newDate1, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime nowDate2 = LocalDateTime.parse(newDate2, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime localDateTime1 = nowDate1.plusHours(8);
            LocalDateTime localDateTime2 = nowDate2.plusHours(8);
            str1 = localDateTime1.toString();
            str2 = localDateTime2.toString();
        }
        List<Article> articles = articleMapper.selectArticlesOfLogicDeleted2(categoryId, str1, str2);
        return articles;
    }

    @Override
    public Article selectArtById(Integer id) {
        return articleMapper.selectArtById(id);
    }

    @Override
    public List<ArticleAndUser> findAllArticlesByCN(Map<String, String> requestBody) {
        String categoryName = requestBody.get("categoryName");
        Integer page = Integer.valueOf(requestBody.get("page"));
        Integer pageSize = Integer.valueOf(requestBody.get("pageSize"));
        Integer offset = (pageSize - 1) * page;
        return articleMapper.findAllArticlesByCN(categoryName , page , offset);
    }

    @Override
    public void update(Article article) {
        //如果state == '未通过' 需要及时删除redis中对应存储的数据
        User user = userMapper.selectById(article.getCreateUser());
        if ("未通过".equals(article.getState())){
            //发布 文章状态由 '未通过' -> '审核中'
            //1. 删除redis中MESSAGE_STORED键对应的数据
            redisService.deleteMessageFromRedis(user.getUsername() , String.valueOf(article.getId()));
            //2. 删除MySQL中message表
            //messageMapper.deleteByArticleId(article.getId());
        }
        if (article.getDeleted() == 1){
            article.setState("发布中");
        }else {
            article.setState("草稿");
        }
        articleMapper.updateArticle(article);
    }

    @Override
    public void deleteById(Integer id) {
        articleMapper.deleteByArticleId("create_user", id);
        //还要删除掉收藏中的数据
        articleMapper.deleteArticleById(id);
    }


    //添加文章到我的收藏
    @Override
    public void addArticle(Integer articleId) {
        ArticleILike articleILike = new ArticleILike();

        articleILike.setArticleId(articleId);
        articleILike.setCollectionTime(LocalDateTime.now());
        Map<String , Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");

        articleILike.setUserId(uid);

        articleMapper.addArticleToLike(articleILike);
    }

    @Override
    public List<Article> selectArticlesByUserId(Integer uid) {
        List<Article> articles = articleMapper.selectArticleByUserId(uid);
        return articles;
    }



    //主页消息
    @Override
    public List<Article> findWaitingOrNoPass(Integer label) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        return articleMapper.findWaitingOrNoPass(label, uid);
    }

    @Override
    public NumAndRankByAuthor selectNumAndRank(Integer uid) {
        List<NumAndRankByAuthor> numAndRankByAuthors = articleMapper.selectNumAndRank();
        for (NumAndRankByAuthor numAndRankByAuthor : numAndRankByAuthors) {
            if (uid.equals(numAndRankByAuthor.getCreateUser())){
                return numAndRankByAuthor;
            }
        }
        return null;
    }

    //推荐
    @Override
    public List<ArticleAndUser> showRecommendArticle(RequestParams params) {
        return articleMapper.recommendArticleSearch(params.getSize() , params.getPage());
    }

    //头条数据
    @Override
    public List<Article> hotSearchService2(RequestParams params) {
        // offset: 数据查询起点
        Integer offset = params.getPage();
        // limit: 一次查询数量
        Integer limit = params.getSize();
        return articleMapper.hostSearch2(limit, offset);
    }

    //热点数据
    @Override
    public List<Article> hotpotFindService(RequestParams params) {
        Integer offset = params.getPage();
        Integer limit = params.getSize();
        return articleMapper.hotpotFind(limit, offset);
    }

    //ES 搜索
    @Override
    public PageBean<ArticleAndUser> search(RequestParams params) {
        try {
            SearchRequest request = new SearchRequest("article");
            Integer cid = params.getCategoryId();
            String key = params.getKey();
            String timeRange  = params.getUpdateTime();
            // 构建基础查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if (key != null && !key.isEmpty()) {
                boolQuery.must(QueryBuilders.multiMatchQuery(key, "title", "content"));
            }
            if (cid != null) {
                boolQuery.filter(QueryBuilders.termQuery("categoryId", cid));
            }
            // 添加 deleted = 0 的过滤条件
            boolQuery.filter(QueryBuilders.termQuery("deleted", 0));
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            // 如果提供了有效的时间范围参数，则添加时间范围过滤条件
            if (timeRange != null && !timeRange.trim().isEmpty()) {
                ZonedDateTime now = ZonedDateTime.now(zoneId);
                ZonedDateTime cutoff = switch (timeRange) {
                    case "week" -> now.minusWeeks(1);
                    case "month" -> now.minusMonths(1);
                    case "threeMonths" -> now.minusMonths(3);
                    case "year" -> now.minusYears(1);
                    default -> throw new IllegalArgumentException("Invalid time range parameter: " + timeRange);
                };
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(zoneId);
                String formattedCutoff = cutoff.format(formatter);
                String formattedNow = now.format(formatter);
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("updateTime")
                        .gte(formattedCutoff)
                        .lte(formattedNow);
                boolQuery.filter(rangeQueryBuilder);
                // 设置按 updateTime 升序排序
                request.source().sort("updateTime", SortOrder.DESC);
            }else {
                // 默认按照 _score 排序
                request.source().sort("_score", SortOrder.DESC);
            }

            // 设置查询源
            request.source().query(boolQuery);
            request.source().highlighter(new HighlightBuilder()
                    .field(new HighlightBuilder.Field("title").numOfFragments(1).fragmentSize(30))
                    .field(new HighlightBuilder.Field("content").numOfFragments(1).fragmentSize(200))
                    .requireFieldMatch(false));
            // 分页
            int page = params.getPage();
            int size = params.getSize();
            request.source().from((page - 1) * size).size(size);
            // 发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return HandleResponseUtil.handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //批量删除
    @Override
    public void deleteByIds(List<Integer> ids) {
        String value = ids.toString().replaceAll("[\\[\\]]", "");
        articleMapper.deleteByIds(value);
    }

    @Override
    public List<CategoryArticleCount> selectArticleByCategory() {
        return articleMapper.selectArticleByCategory();
    }

    //删除掉收藏中的数据
    @Override
    public void deArticleCollectionById(Integer articleCollectionId) {
        articleMapper.deleteArticleById(articleCollectionId);
    }

    //同步es数据
    @Override
    public void insertById(Integer id) {
        try {
            Article article = getById(id);
            // 1.准备reques对象
            IndexRequest request = new IndexRequest("article").id(String.valueOf(article.getId()));
            // 2.准备JSON文档
            request.source(JSON.toJSONString(article), XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteByIdFromES(Integer id) {
        try {
            DeleteRequest request = new DeleteRequest("article" , id.toString());
            client.delete(request , RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void auditIsPassService(String userId, Integer articleId, String sendId , String message) {
        //1.数据保存到redis中
        redisService.saveMessageAndUser2Redis(userId, articleId , message);
        //2.保存到mysql中
        messageMapper.addMessage(new Message(userId , articleId ,sendId , message));
    }

    /**
     * 如果数据量较大，可以采用批量查询优化
     * */
    @Override
    public List<ArticleAndLikeCount> HotEssayListService(int number) {
        ArrayList<ArticleAndLikeCount> articlesList = new ArrayList<>();
        List<LikedCount> likedCounts = articleMapper.HotEssayList(number);
        for (LikedCount likedCount : likedCounts) {
            ArticleAndLikeCount articleAndLikeCount = new ArticleAndLikeCount();
            Article article1 = articleMapper.selectArtById(Integer.valueOf(likedCount.getLikedUserId()));
            User user = userMapper.selectById(article1.getCreateUser());
            articleAndLikeCount.setId(article1.getId());
            articleAndLikeCount.setTitle(article1.getTitle());
            articleAndLikeCount.setUsername(user.getUsername());
            articleAndLikeCount.setCreateUser(user.getId());
            articleAndLikeCount.setNumber(likedCount.getNumber());
            articlesList.add(articleAndLikeCount);
        }
        return articlesList;
    }

    @Override
    public List<ArticleAndLikeCount> findArticleAndLikeCount(int createUser , int deleted) {
        return articleMapper.findArticleAndLikeCount(createUser , deleted);
    }


}
