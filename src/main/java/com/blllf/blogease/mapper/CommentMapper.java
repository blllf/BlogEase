package com.blllf.blogease.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blllf.blogease.pojo.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select * from comment")
    public List<Comment> selectAll();


    //添加一个评论
    @Insert("insert into comment(user_id, content,  create_time, parent_comment_id , article_id) " +
            "values (#{userId} , #{content} , now() , #{parentCommentId} , #{articleId})")
    public int saveComment(Comment comment);


    //查询父级评论
    @Select("select * from comment where parent_comment_id = #{parentId} and article_id = #{articleId} order by create_time desc")
    public List<Comment> findByParentId(Long parentId , Long articleId);


    @Select("SELECT c.*, u.username, u.user_pic " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.article_id = #{articleId} " +
            "ORDER BY c.create_time DESC") // 按时间正序保证父评论在前
    List<Comment> findAllByArticleIdWithUser(Long articleId);


    //删除评论





}
