package com.blllf.blogease.util;

import io.swagger.v3.oas.models.security.SecurityScheme;

public class RedisKeyUtil {

    //保存用户点赞数据的key
    public static final String MAP_KEY_USER_LIKED = "MAP_USER_LIKED";

    //保存用户被点赞数量的key
    public static final String MAP_KEY_USER_LIKED_COUNT = "MAP_USER_LIKED_COUNT";

    //消息存储
    public static final String MESSAGE_STORED = "MESSAGE_STORED";

    //用户粉丝数
    public static final String FOLLOWER_COUNT = "FOLLOWER_COUNT";
    //用户关注数
    public static final String ATTENTION_COUNT = "ATTENTION_COUNT";

    /*
    * 拼接被点赞的用户id和点赞的人的id作为key。格式 222222::333333
    * */
    public static String getLikedKey(String likedUserId, String likedPostId){
        StringBuilder sb = new StringBuilder();
        sb.append(likedUserId);
        sb.append("::");
        sb.append(likedPostId);
        return sb.toString();
    }

    /*
    * 消息存储格式 username:articleId = key  ; message = value
    * */
    public static String getMessageStoredKey(String userId , Integer articleId){
        StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append("->");
        sb.append(articleId);
        return sb.toString();
    }

    public static String genFollowerCountKey(Integer userId){
        return "user:" + userId + ":follower_count";
    }

    public static String genAttentionCountKey(Integer userId){
        return "user:" + userId + ":attention_count";
    }

    public static String genAttentionRelationKey(Integer userId){
        return "user:" + userId + ":attention";
    }

    public static String genFollowerRelationKey(Integer userId){
        return "user:" + userId + ":followers";
    }


}
