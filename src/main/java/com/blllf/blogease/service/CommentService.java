package com.blllf.blogease.service;

import com.blllf.blogease.pojo.Comment;

import java.util.List;

public interface CommentService {
    public List<Comment> listComment(Long id , Long articleId);

    public void deleteComments(Long parentId,Long articleId);


}
