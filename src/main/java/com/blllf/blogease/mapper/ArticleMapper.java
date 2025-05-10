package com.blllf.blogease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blllf.blogease.pojo.Article;
import com.blllf.blogease.pojo.ArticleILike;
import com.blllf.blogease.pojo.LikedCount;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.pojo.dto.ArticleAndLikeCount;
import com.blllf.blogease.pojo.dto.ArticleAndUser;
import com.blllf.blogease.pojo.dto.CategoryArticleCount;
import com.blllf.blogease.pojo.dto.NumAndRankByAuthor;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Insert("insert into article(title, content, cover_img, state, category_id, create_user, create_time, update_time, deleted) VALUES " +
            "(#{title} , #{content} ,#{coverImg} ,#{state}, #{categoryId} , #{createUser} ,#{createTime} ,#{updateTime} , #{deleted})")
    void add(Article article);

    void adminAdd(Article article);

    Page<Article> list(@Param("uid") Integer uid, @Param("categoryId") Integer categoryId, @Param("deleted") Integer deleted);

    //查找所有的文章 根据categoryId state title
    Page<Article> selectAll(Integer categoryId, String state , List<Integer> authors, String title , String testDate1 , String testDate2);


    @Select("select * from article where id = #{id}")
    Article selectArtById(Integer id);
    @Select("select title , create_user  from article where id in (${ids})")
    List<Article> selectArtByIds(@Param("ids") String ids);


    @Update("update article set title = #{title} , content = #{content} , cover_img = #{coverImg} ,state = #{state} , deleted = #{deleted} , category_id = #{categoryId} , update_time = now() " +
            "where id = #{id}")
    void updateArticle(Article article);

    @Delete("delete from article where id = #{id}")
    void deleteByArticleId(String createUser, Integer id);
    //删除掉收藏中的数据
    @Delete("delete from articlecollection where article_id = #{articleId}")
    void deleteArticleById(Integer articleId);

    //批量删除
    @Delete("delete from article where id in (${ids})")
    void deleteByIds(@Param("ids") String ids);

    //查询不同分类文章的数量
    /*@Select("select category.id, category_id , count(*) as total_count , category_name , category_alias from category , article " +
            "where category.id = category_id and article.state = '已发布' and article.deleted = 0 GROUP BY article.category_id ;")*/
    @Select("select c.id , c.category_name , c.category_alias, c.category_pic, count(a.id) as total_count from category c left join article a on c.id = a.category_id" +
            "     and a.state = '已发布' and a.deleted = 0 group by c.id;")
    List<CategoryArticleCount> selectArticleByCategory();

    /*
    * 收藏文章
    * */
    @Insert("insert into articlecollection(create_user, collection_time, article_id) values (#{userId} , now() , #{articleId})")
    void addArticleToLike(ArticleILike articleILike);



    @Select("select article.* , articlecollection.collection_time from article , articlecollection  " +
            "where articlecollection.create_user = #{userId} and  articlecollection.article_id = article.id " +
            "order by articlecollection.collection_time DESC;")
    List<Article> selectArticleByUserId(Integer userId);

    //分页查询 分类页面
    //Page<Article> findArticlesByPage(Integer page , p)
    List<Article> findArticlesByPage(Integer categoryId ,String username , String title);


    //查询逻辑删除文章, 1==正在审核中
    @Select("select * from article where deleted = #{num}")
    List<Article> selectArticlesOfLogicDeleted(Integer num);
    List<Article> selectArticlesOfLogicDeleted2(Integer categoryId , String date1 , String date2);


    //主页消息
    @Select("select * from article where deleted = #{label} and create_user = #{userId}")
    List<Article> findWaitingOrNoPass(Integer label , Integer userId);
    @Select("select * from article where deleted = #{num} and create_user = #{userId}")
    List<Article> selectArticlesOfLDByUser(Integer num , Integer userId);
    @Select("select * from article where deleted = #{num} and create_user = #{userId}")
    List<Article> selectArticlesOfNoPass(Integer num , Integer userId);

    //管理员审核文章通过或不通过
    @Update("update article set state = #{state}, deleted = #{label} where id = #{articleId}")
    void isPass( Integer label ,String state, Integer articleId);

    //热门搜索
    @Select("select * from article where deleted = 0")
    Page<Article> hostSearch();

    List<ArticleAndUser> recommendArticleSearch(Integer limit, Integer offset);
    //头条数据
    List<Article> hostSearch2(Integer limit , Integer offset);
    //热点数据
    List<Article> hotpotFind(Integer limit, Integer offset);

    //查询作者发布文章数以及排名
    List<NumAndRankByAuthor> selectNumAndRank();

    //根据分类的Name查找对应文章
    List<ArticleAndUser> findAllArticlesByCN(String categoryName , Integer limit, Integer offset);

    //热文榜
    @Select("select * from userlikecount order by number DESC limit #{number}")
    List<LikedCount> HotEssayList(int number);

    //个人中心-文章
    List<ArticleAndLikeCount> findArticleAndLikeCount(int createUser , int deleted);
}
