package com.blllf.blogease.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//省去了get和set还有toString方法
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment{

    private Long id;
    private Integer userId;
    private String content;
    private Date createTime;
    private Long parentCommentId;
    private Long articleId;
    //回复评论
    private List<Comment> replyComments = new ArrayList<>();
    private String username;
    private String userPic;
    private Boolean isExpanded = false;


}

