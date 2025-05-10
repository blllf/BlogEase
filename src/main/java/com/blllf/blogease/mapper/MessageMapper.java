package com.blllf.blogease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blllf.blogease.pojo.Message;
import com.blllf.blogease.pojo.PageBean;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Insert("insert into message(content, receiver_id, article_id, send_id , create_time) " +
            "VALUES (#{content} , #{receiverId} , #{articleId} , #{sendId}  , NOW())")
    void addMessage(Message message);

    @Update("update message set content = #{content} , receiver_id = #{receiverId} , send_id = #{sendId} , create_time = NOW() " +
            "where article_id = #{articleId}")
    void updateMessage(Message message);

    @Delete("delete from message where article_id = #{articleId}")
    void deleteByArticleId(Integer articleId);

    @Select("select * from message where article_id = #{articleId}")
    Message selectMsgById(Integer articleId);

    Page<Message> findMsgList(Integer isBroadcast, String receiverId , Integer isRead, String title );

    //批量删除
    void deleteMsgByIds(List<Integer> ids);

    //批量设为已读
    void updateMsgByIds(List<Integer> ids);
}
