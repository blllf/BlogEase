package com.blllf.blogease.pojo.dto;

import com.blllf.blogease.pojo.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleAndLikeCount extends Article {
    //点赞数量
    private int number;
    //作者姓名
    private String username;
    //文章评论数
    private Integer commentCount;
    //文章收藏数
    private Integer collectionCount;

}
