package com.blllf.blogease.service;

import com.blllf.blogease.pojo.LikedCount;
import com.blllf.blogease.pojo.Message;
import com.blllf.blogease.pojo.UserLike;
import java.util.List;


public interface RedisService {

    //保存消息&用户道redis中
    void saveMessageAndUser2Redis(String userId, Integer articleId , String message);
    //从Redis中删除MESSAGE_STORED键中的一条数据
    public void deleteMessageFromRedis(String userId , String articleId);
    //从Redis中读取用户对应的数据
    public List<Message> receiveMsg(String userId);

    //点赞。状态为1
    /**
     * @param likedUserId 文章id
     * @param likedPostId 用户id
     */
    public void saveLiked2Redis(String likedUserId, String likedPostId);

    //取消点赞。将状态改变为0
    public void unlikeFromRedis(String likedUserId, String likedPostId);

    //从Redis中删除一条点赞数据
    public void deleteLikedFromRedis(String likedUserId, String likedPostId);

    //该用户的点赞数加1 likedUserId：被点赞的用户id
    public void incrementLikedCount(String likedUserId);

    //该用户的点赞数减1
    public void decrementLikedCount(String likedUserId);

    //获取Redis中存储的所有点赞数据
    public List<UserLike> getLikedDataFromRedis();

    //获取Redis中存储的所有点赞数量
    public List<LikedCount> getLikedCountFromRedis();



    // ------------------------------  Redis To Mysql ------------------------------

    //保存点赞记录 到本地数据库(redis 中所有的数据)
    public void addToLocalDB();

    //保存点赞数量到数据库
    public void addCountToLocalDB();

    //从数据库中读取数据到redis
    public void RedForMysLike();

    //从数据库中读取数据到redis
    public void RedCountForMysLike();

    // ------------------------------  Mysql 层操作 ------------------------------

    //保存点赞记录
    public void addLike(UserLike userLike);

    //修改
    public void updateLike(UserLike userLike);





}
