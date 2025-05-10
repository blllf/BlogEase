package com.blllf.blogease.controller;

import com.blllf.blogease.mapper.CommentMapper;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.Comment;
import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentService commentService;

    //添加评论
    @PostMapping("/addComment")
    public Result addComment(@RequestBody Comment comment){
        Long parentCommentId = comment.getParentCommentId();
        if (parentCommentId == null){
            comment.setParentCommentId(Long.parseLong("-1"));
        }
        int i = commentMapper.saveComment(comment);
        if (i > 0)
            return Result.success();
        else
            return Result.error("添加失败");
    }

    //查询父级评论
    @GetMapping("/selectAllByParentId")
    public Result<List<Comment>> selectAllByParentId(Long parentId, Long articleId){
        List<Comment> comments = commentMapper.findByParentId(parentId, articleId);
        return Result.success(comments);
    }

    //查询所有评论
    @GetMapping("/findAllComments")
    public Result<List<Comment>> findAllComments(Long parentId , Long articleId){
        List<Comment> comments = commentService.listComment(parentId, articleId);
        return Result.success(comments);
    }

    //删除评论
    @GetMapping("/deleteComments")
    public Result deleteComments(Long parentId , Long articleId){
        commentService.deleteComments(parentId , articleId);
        return Result.success();
    }


}
