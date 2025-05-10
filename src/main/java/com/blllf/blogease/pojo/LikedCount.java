package com.blllf.blogease.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Entity
public class LikedCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String likedUserId;

    private Integer number;

    public LikedCount() {
    }

    public LikedCount(String likedUserId, Integer number) {
        this.likedUserId = likedUserId;
        this.number = number;
    }

}
