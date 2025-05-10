package com.blllf.blogease.controller;

import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.pojo.UserLike;
import com.blllf.blogease.service.impl.RedisServiceImpl;
import com.blllf.blogease.util.RedisKeyUtil;
import com.blllf.blogease.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/thumbsUp")
public class ThumbsUpController {
    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RedisTemplate<String , Object> redisTemplate;


    //获取文章点赞数据
    @GetMapping("/findLikeCountRedis")
    public Result<UserLike> findLikeCountRedis(String articleId){

        //初始点赞数量
        Integer number = (Integer) redisTemplate.opsForHash().get(RedisKeyUtil.MAP_KEY_USER_LIKED_COUNT, articleId);

        //1.获取点赞用户的ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        //redisService.saveLiked2Redis(articleId, uid);
        //2.用户第一次点赞 查询MAP_KEY_USER_LIKED 中是否存在对该文章已点赞
        String key = RedisKeyUtil.getLikedKey(articleId, String.valueOf(uid));
        Integer status = (Integer) redisTemplate.opsForHash().get(RedisKeyUtil.MAP_KEY_USER_LIKED, key);
        if (status != null){
            //说明 该用户已存在
            if (status == 0){
                // 0 ： 取消点赞
                return Result.success(new UserLike(articleId , String.valueOf(uid) , status , number));
            }else {
                // 1 ：点赞
                return Result.success(new UserLike(articleId , String.valueOf(uid) , status , number));
            }
        }
        // 默认
        status = 0;
        //3. status == null -> 该用户未对该文章操作
        return Result.success(new UserLike(articleId , String.valueOf(uid) , status , number));
    }

    //该用户的点赞数加1
    @GetMapping("/incrementCount")
    public Result incrementCount(String articleId){
        //1.获取点赞用户的ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        String key = RedisKeyUtil.getLikedKey(articleId, String.valueOf(uid));

        //2.判断该key 是否存在
        //Boolean b = redisTemplate.opsForHash().hasKey(RedisKeyUtils.MAP_KEY_USER_LIKED, key);
        Integer status = (Integer) redisTemplate.opsForHash().get(RedisKeyUtil.MAP_KEY_USER_LIKED, key);
        redisService.saveLiked2Redis(articleId , String.valueOf(uid));
        redisService.incrementLikedCount(articleId);
        return Result.success(1);
    }


    //取消点赞 -1
    @GetMapping("/decrementCount")
    public Result decrementCount(String articleId){
        //1. 用户信息 处 改变status状态 1 ：已点赞 0： 待点赞
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        //写入redis
        redisService.unlikeFromRedis(articleId , String.valueOf(uid));
        //2.改变数量 -1
        redisService.decrementLikedCount(articleId);
        //把status状态写入数据库
        return Result.success();
    }


}
