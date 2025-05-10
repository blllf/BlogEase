package com.blllf.blogease.service.impl;

import com.blllf.blogease.enums.LikedStatusEnum;
import com.blllf.blogease.mapper.ThumbsUpMapper;
import com.blllf.blogease.pojo.LikedCount;
import com.blllf.blogease.pojo.Message;
import com.blllf.blogease.pojo.UserLike;
import com.blllf.blogease.service.RedisService;

import com.blllf.blogease.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThumbsUpMapper thumbsUpMapper;

    //保存消息&用户道redis中
    @Override
    public void saveMessageAndUser2Redis(String userId, Integer articleId, String message) {
        String key = RedisKeyUtil.getMessageStoredKey(userId, articleId);
        redisTemplate.opsForHash().put(RedisKeyUtil.MESSAGE_STORED , key , message );
        //System.out.println("Session saved to Redis: " + userId);
    }

    @Override
    public void deleteMessageFromRedis(String userId, String articleId) {
        String key = RedisKeyUtil.getMessageStoredKey(userId, Integer.valueOf(articleId));
        redisTemplate.opsForHash().delete(RedisKeyUtil.MESSAGE_STORED , key);
    }

    //从Redis中读取用户对应的数据
    public List<Message> receiveMsg(String userId){
        ArrayList<Message> list = new ArrayList<>();
        // 定义一个符合你的需求的模式
        String pattern = userId + "->*"; 
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        //ScanOptions.NONE为获取全部键对
        Cursor<Map.Entry<Object,Object>> scan = redisTemplate.opsForHash().scan(RedisKeyUtil.MESSAGE_STORED , options);
        while (scan.hasNext()){
            Map.Entry<Object, Object> entry = scan.next();
            String key = (String) entry.getKey();
            String[] split = key.split("->");
            //String username = split[0];
            Integer article = Integer.valueOf(split[1]);
            String message = (String) entry.getValue();
            list.add(new Message(message , article));
            //System.out.println(username + '\n' + article + '\n' + message);
        }
        return list;
    }

    //点赞。状态为1
    //MAP_KEY_USER_LIKED：保存用户点赞数据的key
    @Override
    public void saveLiked2Redis(String likedUserId, String likedPostId) {
        String key = RedisKeyUtil.getLikedKey(likedUserId, likedPostId);
        //System.out.println("key: " + key);
        redisTemplate.opsForHash().put(RedisKeyUtil.MAP_KEY_USER_LIKED , key , LikedStatusEnum.LIKE.getCode());
    }

    //取消点赞。将状态改变为0
    @Override
    public void unlikeFromRedis(String likedUserId, String likedPostId) {
        String key = RedisKeyUtil.getLikedKey(likedUserId, likedPostId);
        redisTemplate.opsForHash().put(RedisKeyUtil.MAP_KEY_USER_LIKED , key , LikedStatusEnum.UNLIKE.getCode());
    }


    //从Redis中删除一条点赞数据
    @Override
    public void deleteLikedFromRedis(String likedUserId, String likedPostId) {
        String key = RedisKeyUtil.getLikedKey(likedUserId, likedPostId);
        redisTemplate.opsForHash().delete(RedisKeyUtil.MAP_KEY_USER_LIKED , key);
    }

    //该用户的点赞数加1
    @Override
    public void incrementLikedCount(String likedUserId) {
        redisTemplate.opsForHash().increment(RedisKeyUtil.MAP_KEY_USER_LIKED_COUNT , likedUserId , 1);
    }

    //该用户的点赞数减1
    @Override
    public void decrementLikedCount(String likedUserId) {
        redisTemplate.opsForHash().increment(RedisKeyUtil.MAP_KEY_USER_LIKED_COUNT , likedUserId , -1);
    }

    //获取Redis中存储的所有点赞用户数据
    @Override
    public List<UserLike> getLikedDataFromRedis() {
        //ScanOptions.NONE为获取全部键对
        Cursor<Map.Entry<Object,Object>> scan = redisTemplate.opsForHash().scan(RedisKeyUtil.MAP_KEY_USER_LIKED, ScanOptions.NONE);
        ArrayList<UserLike> list = new ArrayList<>();

        while (scan.hasNext()){
            Map.Entry<Object, Object> entry = scan.next();
            String key = (String) entry.getKey();
            String[] split = key.split("::");
            String likedUserId = split[0];
            String likedPostId = split[1];
            // 点赞状态
            Integer value = (Integer) entry.getValue();

            UserLike userLike = new UserLike(likedUserId, likedPostId, value);
            list.add(userLike);
            //您可能正在处理用户的点赞数据，并且一旦这些数据被处理（例如，转化为UserLike对象并添加到列表中），就不再需要保留在Redis中了。
            //redisTemplate.opsForHash().delete(RedisKeyUtils.MAP_KEY_USER_LIKED , key);
        }
        return list;
    }

    //获取Redis中存储的所有点赞数量
    @Override
    public List<LikedCount> getLikedCountFromRedis() {
        Cursor<Map.Entry<Object,Object>> scan = redisTemplate.opsForHash().scan(RedisKeyUtil.MAP_KEY_USER_LIKED_COUNT, ScanOptions.NONE);
        ArrayList<LikedCount> list = new ArrayList<>();
        while (scan.hasNext()){
            Map.Entry<Object, Object> entry = scan.next();
            String key = (String) entry.getKey();
            LikedCount likedCount = new LikedCount(key , (Integer) entry.getValue());
            list.add(likedCount);
            //从redis中删除
            //redisTemplate.opsForHash().delete(RedisKeyUtils.MAP_KEY_USER_LIKED_COUNT , key);
        }
        return list;
    }




    // ------------------------------  Redis To Mysql ------------------------------

    //保存点赞记录 到本地数据库(redis 中所有的数据)
    @Override
    public void addToLocalDB() {
        List<UserLike> userLikes = getLikedDataFromRedis();
        for (UserLike userLike : userLikes) {
            UserLike u = thumbsUpMapper.getByLikedUserIdAndLikedPostId(userLike.getLikedUserId(), userLike.getLikedPostId());
            if (u != null){
                updateLike(userLike);
            }else {
                addLike(userLike);
            }
        }
    }

    public void addToLocalDB2() {
        List<UserLike> userLikes = getLikedDataFromRedis();
        for (UserLike userLike : userLikes) {
            //添加之后删除redis中的数据
            String likedUserId = userLike.getLikedUserId();
            String likedPostId = userLike.getLikedPostId();
            String key = RedisKeyUtil.getLikedKey(likedUserId, likedPostId);
            redisTemplate.opsForHash().delete(RedisKeyUtil.MAP_KEY_USER_LIKED , key);
            UserLike u = thumbsUpMapper.getByLikedUserIdAndLikedPostId(userLike.getLikedUserId(), userLike.getLikedPostId());
            //System.err.println(userLike.getLikedPostId() + "  " + userLike.getStatus());
            if (u != null){
                updateLike(userLike);
            }else {
                addLike(userLike);
            }
        }
    }

    //保存点赞数量到数据库
    //定时保存
    @Override
    public void addCountToLocalDB() {
        List<LikedCount> likedCounts = getLikedCountFromRedis();
        for (LikedCount likedCount : likedCounts) {
            //本地数据库已存在的用户信息
            LikedCount count = thumbsUpMapper.findById(likedCount.getLikedUserId());
            if (count != null){
                //更新点赞数量
                thumbsUpMapper.updateLikeCount(likedCount.getNumber() , count.getLikedUserId());
            }else {
                thumbsUpMapper.addLikeCountToDB(likedCount);
            }
        }
    }
    //程序停止保存
    public void addCountToLocalDB2() {
        //获取redis中所有的数据封装成LikedCount对象
        List<LikedCount> likedCounts = getLikedCountFromRedis();
        for (LikedCount likedCount : likedCounts) {
            redisTemplate.opsForHash().delete(RedisKeyUtil.MAP_KEY_USER_LIKED_COUNT , likedCount.getLikedUserId());
            //本地数据库已存在的用户信息
            LikedCount count = thumbsUpMapper.findById(likedCount.getLikedUserId());
            if (count != null){
                //更新点赞数量
                thumbsUpMapper.updateLikeCount(likedCount.getNumber() , count.getLikedUserId());
            }else {
                thumbsUpMapper.addLikeCountToDB(likedCount);
            }
        }
    }


    //从数据库中读取数据到redis
    @Override
    public void RedForMysLike() {
        List<UserLike> userLikes = thumbsUpMapper.selectAll();
        for (UserLike ul : userLikes) {
            String likedUserId = ul.getLikedUserId();
            String likedPostId = ul.getLikedPostId();
            Integer status = ul.getStatus();
            String key = RedisKeyUtil.getLikedKey(likedUserId, likedPostId);
            redisTemplate.opsForHash().put(RedisKeyUtil.MAP_KEY_USER_LIKED , key , status);
        }
    }

    @Override
    public void RedCountForMysLike() {
        List<LikedCount> likedCounts = thumbsUpMapper.findAll();
        for (LikedCount lc : likedCounts) {
            String likedUserId = lc.getLikedUserId();
            Integer number = lc.getNumber();
            redisTemplate.opsForHash().put(RedisKeyUtil.MAP_KEY_USER_LIKED_COUNT , likedUserId , number);
        }
    }


    // ------------------------------  Mysql 层操作 ------------------------------

    //用户
    //保存点赞记录
    @Override
    public void addLike(UserLike userLike) {
        thumbsUpMapper.save(userLike);
    }

    //修改
    @Override
    public void updateLike(UserLike userLike) {
        thumbsUpMapper.updateLike(userLike);
    }




}
