package com.blllf.blogease.service.impl;

import com.blllf.blogease.mapper.CommentMapper;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.Comment;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplys = new ArrayList<>();


    @Override
    public List<Comment> listComment(Long id, Long articleId) {
        //查询到父节点
        List<Comment> comments = commentMapper.findByParentId(id , articleId);
        for (Comment comment : comments) {
            Long eachId = comment.getId();
            Integer userId = comment.getUserId();
            User user = userMapper.selectById(userId);
            List<Comment> childComments = commentMapper.findByParentId(eachId , articleId);
            combineChildren(childComments , articleId );
            comment.setUsername(user.getUsername());
            comment.setUserPic(user.getUserPic());
            comment.setReplyComments(tempReplys);
            tempReplys = new ArrayList<>();
        }
        return comments;
    }



    //查询子评论
    private void combineChildren(List<Comment> childComments , Long articleId ){
        if (!childComments.isEmpty()){
            for (Comment childComment : childComments) {
                Integer uid = childComment.getUserId();
                User user = userMapper.selectById(uid);
                childComment.setUserId(uid);
                childComment.setUsername(user.getUsername());
                childComment.setUserPic(user.getUserPic());
                tempReplys.add(childComment);
                Long id = childComment.getId();
                AllChildrenComments(id , articleId);
            }
        }
    }


    //查询二级子评论
    private void AllChildrenComments(Long childId , Long articleId){
        List<Comment> comments = commentMapper.findByParentId(childId , articleId);
        if (!comments.isEmpty()){
            for (Comment comment : comments) {
                Integer uid = comment.getUserId();
                Long id = comment.getId();
                User user = userMapper.selectById(uid);
                comment.setUsername(user.getUsername());
                comment.setUserPic(user.getUserPic());
                comment.setUserId(uid);
                tempReplys.add(comment);
                //循环迭代找到所有的子集回复
                AllChildrenComments(id , articleId);
            }
        }
    }

    //删除评论
    @Override
    public void deleteComments(Long parentId,Long articleId){
        //先把父评论删掉 然后再删除数据库里的评论
        commentMapper.deleteById(parentId);
        List<Comment> comments = commentMapper.findByParentId(parentId, articleId);
        if (comments != null){
            for (Comment comment : comments) {
                commentMapper.deleteById(comment.getId());
                Long id = comment.getId();
                deleteComments(id , articleId);
            }
        }
    }



}
