package com.blllf.blogease.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class Follow {
    @NotNull
    private Integer followerId;   // 关注者ID
    @NotNull
    private Integer followingId;  // 被关注者ID
    @NotNull
    private Date createdAt;
}
