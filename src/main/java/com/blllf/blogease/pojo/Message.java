package com.blllf.blogease.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    private Integer id;
    private String content;
    private String receiverId;
    private Integer articleId;
    private String sendId;
    private Integer isRead;
    private LocalDateTime createTime;
    private Integer isBroadcast;

    // 新添加的字段，用于封装article表中的title数据
    @TableField(exist = false)
    private String articleTitle;

    public Message(String receiverId , Integer articleId , String sendId, String content){
        this.receiverId = receiverId;
        this.articleId = articleId;
        this.sendId = sendId;
        this.content = content;
    }

    public Message(String content , Integer articleId){
        this.articleId = articleId;
        this.content = content;
    }

    public Message(Integer id, String content, String receiverId, Integer articleId, String sendId, Integer isRead, LocalDateTime createTime, Integer isBroadcast) {
        this.id = id;
        this.content = content;
        this.receiverId = receiverId;
        this.articleId = articleId;
        this.sendId = sendId;
        this.isRead = isRead;
        this.createTime = createTime;
        this.isBroadcast = isBroadcast;
    }
}
