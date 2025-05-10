package com.blllf.blogease.service.impl;

import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.service.FocusRedisService;
import com.blllf.blogease.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FocusRedisServiceImpl implements FocusRedisService {
    //构造方法注入
    private final RedisTemplate<String , Object> redisTemplate;
    private final UserMapper userMapper;

    @Override
    public void incrementFollowerCount(Integer userId) {
        String key = RedisKeyUtil.genFollowerCountKey(userId);
        redisTemplate.opsForHash().increment(RedisKeyUtil.FOLLOWER_COUNT , key , 1);
    }

    @Override
    public void incrementFollowingCount(Integer userId) {
        String key = RedisKeyUtil.genAttentionCountKey(userId);
        redisTemplate.opsForHash().increment(RedisKeyUtil.ATTENTION_COUNT , key , 1);
    }

    @Override
    public void decrementFollowerCount(Integer userId) {
        String key = RedisKeyUtil.genFollowerCountKey(userId);
        redisTemplate.opsForHash().increment(RedisKeyUtil.FOLLOWER_COUNT , key , -1);
    }

    @Override
    public void decrementFollowingCount(Integer userId) {
        String key = RedisKeyUtil.genAttentionCountKey(userId);
        redisTemplate.opsForHash().increment(RedisKeyUtil.ATTENTION_COUNT , key , -1);
    }

    /**
     * 使用Sorted Set存储关注关系
     * followerId（A） 关注了 followingId（B）
     * 查询用户A关注的所有人（按时间倒序）
     *
     * @param followerId
     * @param followingId
     */
    @Override
    public void addFollowing(Integer followerId, Integer followingId) {
        String key = RedisKeyUtil.genAttentionRelationKey(followerId);
        redisTemplate.opsForZSet().add(key, followingId, System.currentTimeMillis() );
    }

    /**
     * B被A关注了
     * 查询查询用户B的所有粉丝（按关注时间排序）
     *
     * @param followingId
     * @param followerId
     */
    @Override
    public void addFollower(Integer followingId, Integer followerId) {
        String key = RedisKeyUtil.genFollowerRelationKey(followingId);
        redisTemplate.opsForZSet().add(key, followerId, System.currentTimeMillis());
    }

    @Override
    public void deleteFollow(Integer followerId, Integer followingId) {
        String key1 = RedisKeyUtil.genAttentionRelationKey(followerId);
        String key2 = RedisKeyUtil.genFollowerRelationKey(followingId);
        redisTemplate.opsForZSet().remove(key1 , followingId);
        redisTemplate.opsForZSet().remove(key2 , followerId);
    }

    public Boolean existsFollow(Integer followerId, Integer followingId){
        String key = RedisKeyUtil.genAttentionRelationKey(followerId);
        Double score = redisTemplate.opsForZSet().score(key, followingId);
        if (score != null){
            return true;
        }
        return false;
    }

    @Override
    public int getFollowerCount(Integer userId) {
        String key = RedisKeyUtil.genFollowerCountKey(userId);
        Object value = redisTemplate.opsForHash().get(RedisKeyUtil.FOLLOWER_COUNT , key);
        return value != null ? ((Number) value).intValue() : 0;
    }

    //查询用户关注账号
    @Override
    public List<User> getUserAttentions(Integer userId) {
        ArrayList<Integer> idList = new ArrayList<>();
        String key = RedisKeyUtil.genAttentionRelationKey(userId);
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        if (tuples != null){
            for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                Object value = tuple.getValue();
                idList.add((Integer) value);
            }
        }
        return userMapper.findUsersByIds(idList);
    }


}
