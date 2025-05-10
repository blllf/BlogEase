package com.blllf.blogease.util;

import com.alibaba.fastjson.JSON;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.Article;
import com.blllf.blogease.pojo.PageBean;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.pojo.dto.ArticleAndUser;
import com.blllf.blogease.service.UserService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * 解析es响应数据
 *
 */
@Component
public class HandleResponseUtil {
    private static UserMapper userMapper;
    @Autowired
    public  HandleResponseUtil(UserMapper userMapper){
        HandleResponseUtil.userMapper = userMapper;
    }

    public static PageBean<ArticleAndUser> handleResponse(SearchResponse response){
        //1. 解析响应
        SearchHits searchHits = response.getHits();
        //1.1 获取总条数
        long value = searchHits.getTotalHits().value;
        //1.2 获取文档数组
        SearchHit[] hits = searchHits.getHits();
        ArrayList<ArticleAndUser> articles = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            ArticleAndUser article = JSON.parseObject(json, ArticleAndUser.class);
            //高亮处理
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)){
                //更新字段createUser对应的username
                User user = userMapper.selectById(article.getCreateUser());
                article.setUsername(user.getUsername());
                //根据字段名获取高亮结果
                HighlightField hf = highlightFields.get("title");
                HighlightField hfContent = highlightFields.get("content");
                if (hf != null){
                    String title = hf.getFragments()[0].string();
                    article.setTitle(title);
                }
                if (hfContent != null) {
                    String content = hfContent.getFragments()[0].string();
                    article.setContent(content);
                }
            }
            articles.add(article);
        }
        return new PageBean<>(value , articles);
    }
}
