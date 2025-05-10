package com.blllf.blogease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blllf.blogease.pojo.Article;
import com.blllf.blogease.pojo.PageBean;
import com.blllf.blogease.pojo.dto.*;

import java.util.List;
import java.util.Map;

public interface ArticleService extends IService<Article> {
    void adminAdd(Article article);
    void add(Article article);

    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, Integer deleted);

    //查询所有
    PageBean<Article> selectAll(Integer pageNum, Integer pageSize, Integer categoryId, String title, String author , String testDate1, String testDate2);

    List<Article> selectArticlesAudit(Integer categoryId , String date1 , String date2);

    Article selectArtById(Integer id);

    //根据分类的Name查找对应文章
    List<ArticleAndUser> findAllArticlesByCN(Map<String, String> requestBody);

    void update(Article article);

    void deleteById(Integer id);

    //添加文章到我的收藏
    void addArticle(Integer articleId);
    List<Article> selectArticlesByUserId(Integer uid);

    //主页 消息
    List<Article> findWaitingOrNoPass(Integer label);

    NumAndRankByAuthor selectNumAndRank(Integer uid);

    //推荐
    List<ArticleAndUser> showRecommendArticle(RequestParams params);

    //热点数据
    List<Article> hotSearchService2(RequestParams params);

    //头条数据
    List<Article> hotpotFindService(RequestParams params);

    //首页查询(ES搜索)
    PageBean<ArticleAndUser> search(RequestParams params);

    //批量删除
    void deleteByIds(List<Integer> ids);

    //查询不同分类文章的数量
    List<CategoryArticleCount> selectArticleByCategory();
    void deArticleCollectionById(Integer articleCollectionId);

    //mq 同步es数据
    void insertById(Integer id);
    void deleteByIdFromES(Integer id);

    //文章审核
    void auditIsPassService(String userId , Integer articleId , String sendId , String message);

    //热文榜
    List<ArticleAndLikeCount> HotEssayListService(int number);

    //个人中心-文章
    List<ArticleAndLikeCount> findArticleAndLikeCount(int createUser , int deleted);
}
