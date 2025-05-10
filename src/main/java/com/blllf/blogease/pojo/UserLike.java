package com.blllf.blogease.pojo;

import com.blllf.blogease.enums.LikedStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

//用户点赞表
@Data
@Entity
public class UserLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String likedUserId;
    private String likedPostId;

    private Integer status = LikedStatusEnum.UNLIKE.getCode();  //点赞的状态.默认未点赞

    private Integer number;

    public UserLike() {
    }

    public UserLike(String likedUserId, String likedPostId, Integer status) {
        this.likedUserId = likedUserId;
        this.likedPostId = likedPostId;
        this.status = status;
    }

    public UserLike(String likedUserId, String likedPostId, Integer status , Integer number) {
        this.likedUserId = likedUserId;
        this.likedPostId = likedPostId;
        this.status = status;
        this.number = number;
    }
}
