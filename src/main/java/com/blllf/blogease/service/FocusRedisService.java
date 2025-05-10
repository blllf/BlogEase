package com.blllf.blogease.service;

import com.blllf.blogease.pojo.User;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public interface FocusRedisService {

    // 粉丝数 + 1
    void incrementFollowerCount(Integer userId);
    //关注数 + 1
    void incrementFollowingCount(Integer userId);
    //粉丝数 - 1
    void decrementFollowerCount(Integer userId);
    //关注数 - 1
    void decrementFollowingCount(Integer userId);

    /**
     * 使用Sorted Set存储关注关系
     * followerId（A） 关注了 followingId（B）
     * 查询用户A关注的所有人（按时间倒序）
     * */
    void addFollowing(Integer followerId, Integer followingId);
    /**
     * B被A关注了
     * 查询查询用户B的所有粉丝（按关注时间排序）
     * */
    void addFollower(Integer followingId, Integer followerId);
    //删除对应关系
    void deleteFollow(Integer followerId , Integer followingId);

    //判断关系是否存在
    Boolean existsFollow(Integer followerId, Integer followingId);

    //获得粉丝数
    public int getFollowerCount(Integer userId);

    List<User> getUserAttentions(Integer userId);


}
