package com.blllf.blogease.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blllf.blogease.mapper.ArticleMapper;
import com.blllf.blogease.mapper.CategoryMapper;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.*;
import com.blllf.blogease.pojo.dto.*;
import com.blllf.blogease.service.ArticleService;
import com.blllf.blogease.util.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    //admin 新增文章
    @PostMapping("/admin/add")
    public Result adminAdd(@RequestBody @Validated Article article){
        articleService.adminAdd(article);
        //同步数据
        articleService.insertById(article.getId());
        return Result.success();
    }

    //普通用户 新增文章
    @PostMapping
    public Result add(@RequestBody @Validated Article article){
        articleService.add(article);
        return Result.success();
    }


    //个人中心我的贴子
    @GetMapping
    public Result<PageBean<Article>> list(Integer pageNum , Integer pageSize ,
                                          @RequestParam(required = false) Integer categoryId ,
                                          @RequestParam(required = false) Integer deleted){

        PageBean<Article> list =articleService.list(pageNum , pageSize , categoryId  , deleted);

        return Result.success(list);
    }

    //查询网站总贴
    @GetMapping("/selectAll")
    public Result<PageBean<Article>> selectAll(Integer pageNum , Integer pageSize ,
                                               @RequestParam(required = false) Integer categoryId ,
                                               @RequestParam(required = false) String title,
                                               @RequestParam(required = false) String author,
                                               @RequestParam(required = false) String testDate1,
                                               @RequestParam(required = false) String testDate2){
        if (testDate1 != null ^ testDate2 != null) {
            return Result.error("时间格式不完整");
        }
        PageBean<Article> articles = articleService.selectAll(pageNum, pageSize, categoryId, title , author, testDate1 , testDate2);
        return Result.success(articles);
    }


    //获取文章基本详情
    @GetMapping("/detail")
    public Result<Article> selectArtById(@Validated @NotNull Integer id){
        Article article = articleService.selectArtById(id);
        return Result.success(article);
    }

    //更新文章
    @PutMapping
    public Result update(@RequestBody @Validated Article article){
        articleService.update(article);
        //1.普通用户修改文章后发布 同步es数据
        if (article.getDeleted() == 2){
            articleService.deleteByIdFromES(article.getId());
        }
        //2.admin修改文章后发布 同步es数据
        if (article.getDeleted() == 0){
            articleService.insertById(article.getId());
        }
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@Validated @NotNull Integer id){
        articleService.deleteById(id);
        //同步es数据
        articleService.deleteByIdFromES(id);
        return Result.success();
    }

    //查询不同分类文章的数量
    @GetMapping("/selectArticleByCategory")
    public Result<List<CategoryArticleCount>> selectArticleByCategory(){
        List<CategoryArticleCount> cac = articleService.selectArticleByCategory();
        return Result.success(cac);
    }

    //文章收藏
    @GetMapping("/addArticleILike")
    public Result addArticleILike(@NotNull Integer articleId){
        articleService.addArticle(articleId);
        return Result.success();
    }
    @GetMapping("/selectForCollection")
    public Result<List<Article>> selectForCollection(@RequestParam Integer uid){
        List<Article> articles = articleService.selectArticlesByUserId(uid);
        return Result.success(articles);
    }

    //删除掉收藏中的数据
    @GetMapping("/deleteArticleById")
    public Result deleteArticleById(@NotNull Integer articleId ){
        articleService.deArticleCollectionById(articleId);
        return Result.success();
    }


    //批量删除
    @PostMapping("/deleteByIds")
    public Result deleteByIds(@NotNull @RequestBody List<Integer> ids){
        articleService.deleteByIds(ids);
        //同步数据
        for (int i = 0; i < ids.size(); i++) {
            articleService.deleteByIdFromES(ids.get(i));
        }
        return Result.success();
    }

    //推荐
    @PostMapping("/showRecommendArticle")
    public Result<List<ArticleAndUser>> showRecommendArticle(@RequestBody RequestParams params){
        List<ArticleAndUser> articles = articleService.showRecommendArticle(params);
        return Result.success(articles);
    }

    //头条展示
    @PostMapping("/hotSearch2")
    public Result<List<Article>> hotSearch2(@RequestBody RequestParams params){
        List<Article> articles = articleService.hotSearchService2(params);
        return Result.success(articles);
    }

    //热点展示
    @PostMapping("/hotpotFind")
    public Result<List<Article>> hotpotFind(@RequestBody RequestParams params){
        List<Article> articles = articleService.hotpotFindService(params);
        return Result.success(articles);
    }

    //首页查询(模糊查询) 目前不用
    @GetMapping("/showPageArticle")
    public Result<PageBean<Article>> showPageArticle(Integer pageNum , Integer pageSize ,
                                                     @RequestParam(required = false) Integer categoryId,
                                                     @RequestParam(required = false) String title){
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("state","已发布");
        if (title != null && title != ""){
            wrapper.like("title" , title);
        }
        if (categoryId != null){
            wrapper.eq("category_id",categoryId);
        }
        Page<Article> page = new Page<>(pageNum, pageSize);
        articleMapper.selectPage(page, wrapper);
        //把查询到的数据封装到PageBean中
        PageBean<Article> pb = new PageBean<>();
        pb.setTotal(page.getTotal());
        pb.setItems(page.getRecords());
        return Result.success(pb);
    }

    //首页查询
    //使用es搜索引擎
    @PostMapping("/indexSearch")
    public Result<PageBean<ArticleAndUser>> indexSearch(@RequestBody RequestParams params){
        PageBean<ArticleAndUser> articles = articleService.search(params);
        return Result.success(articles);
    }


    @GetMapping("/id")
    public Result<Article> selectArticleById(Integer id){
        Article article = articleService.selectArtById(id);
        return Result.success(article);
    }

    //根据分类的id查找文章
    @PostMapping("/categoryName")
    public Result<List<ArticleAndUser>> selectArticleByCategoryId(@RequestBody Map<String, String> requestBody){
        List<ArticleAndUser> articleAndUsers = articleService.findAllArticlesByCN(requestBody);
        return Result.success(articleAndUsers);
    }

    @GetMapping("/findArticlesByPage")
    public Result<List<Article>> findArticlesByPage(Integer categoryId,
                                                @RequestParam(required = false) String username ,
                                                @RequestParam(required = false) String title){
        if (username != null & username != ""){
            username = "%" + username + "%";
        }
        if (title != null & title != ""){
            title = "%" + title + "%";
        }
        List<Article> articles = articleMapper.findArticlesByPage(categoryId , username, title);
        return Result.success(articles);
    }


    //管理员审核文章
    //根据文章分类名字以及时间搜索
    @GetMapping("/articlesAudit")
    public Result<List<Article>> articlesAudit(@RequestParam(required = false) Integer categoryId,
                                               @RequestParam(required = false) String testDate1,
                                               @RequestParam(required = false) String testDate2){
        if (testDate1 != null ^ testDate2 != null) {
            return Result.error("时间格式不完整");
        }
        List<Article> articles = articleService.selectArticlesAudit(categoryId , testDate1 ,testDate2);
        return Result.success(articles);
    }

    //管理员审核文章通过或不通过
    @GetMapping("/passOrNoPass")
    public Result auditIsPass(Integer label , String state , Integer articleId , String userId , String sendId , String message){
        articleMapper.isPass(label , state , articleId);
        //同步数据到ES中
        if (label == 0){ articleService.insertById(articleId);}
        //保存消息到message表中
        articleService.auditIsPassService(userId , articleId , sendId , message);
        return Result.success();
    }

    //主页 消息
    @GetMapping("/message")
    public Result<List<Article>> findWaitingOrNoPass(Integer label){
        List<Article> articles = articleService.findWaitingOrNoPass(label);
        return Result.success(articles);
    }

    //查询文章总数 分类总数 用户数量
    @GetMapping("/findCount")
    public Result<CategoryArticleCount> findCAndUAndA(){
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state" , "已发布");
        Integer articlCount = Math.toIntExact(articleMapper.selectCount(queryWrapper));

        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        Integer userCount = Math.toIntExact(userMapper.selectCount(queryWrapper1));

        QueryWrapper<Category> queryWrapper2 = new QueryWrapper<>();
        Integer categoryCount = Math.toIntExact(categoryMapper.selectCount(queryWrapper2));

        //使用mybatis-plus 写法获取user表中的create_time字段信息
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        LocalDateTime createTime = userMapper.selectById(uid).getCreateTime();
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 计算两个时间之间的差值
        Integer daysBetween = Math.toIntExact(ChronoUnit.DAYS.between(createTime, now));

        CategoryArticleCount cac = new CategoryArticleCount();
        cac.setArticleCount(articlCount);
        cac.setUserCount(userCount);
        cac.setCategoryCount(categoryCount);
        cac.setAccountDay(daysBetween);

        return Result.success(cac);
    }

    //数量以及排名
    @GetMapping("/numAndRank")
    public Result<NumAndRankByAuthor> selectNumAndRank(@RequestParam Integer uid){
        NumAndRankByAuthor numAndRankByAuthor = articleService.selectNumAndRank(uid);
        return Result.success(numAndRankByAuthor);
    }

    //热文榜
    @GetMapping("/hotEssayList")
    public Result<List<ArticleAndLikeCount>> HotEssayList(@RequestParam int number){
        List<ArticleAndLikeCount> articleAndLikeCounts = articleService.HotEssayListService(number);
        return Result.success(articleAndLikeCounts);
    }

    //个人中心-文章
    @PostMapping("/personalCenterEssay")
    public Result<List<ArticleAndLikeCount>> PersonalCenterEssay(@RequestBody Map<String,Integer > requestBody){
        int createUser = requestBody.get("createUser");
        int deleted = requestBody.get("deleted");
        List<ArticleAndLikeCount> articlesData = articleService.findArticleAndLikeCount(createUser , deleted);
        return Result.success(articlesData);
    }




}
