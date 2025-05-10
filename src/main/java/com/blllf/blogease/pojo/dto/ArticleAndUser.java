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
public class ArticleAndUser extends Article {
    //用户头像地址
    private String userPic;
    //账号
    private String username;
    //点赞数目
    private Integer number;
    //昵称
    private String nickname;
    //文章评论数
    private Integer commentCount;

}
