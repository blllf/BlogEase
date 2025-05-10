package com.blllf.blogease.task;

import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@Service
public class SyncTask {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper userMapper;

    public SyncTask(RedisTemplate<String, Object> redisTemplate, UserMapper userMapper) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
    }

    @Async
    @Scheduled(fixedRate = 60 * 1000)
    public void syncCounterToDB() {
        log.info("开始同步粉丝数计数器...");
        syncCount(RedisKeyUtil.FOLLOWER_COUNT, "用户粉丝", userMapper::updateUserFollowerCount);
        syncCount(RedisKeyUtil.ATTENTION_COUNT, "用户关注", userMapper::updateUserAttentionCount);
    }

    private void syncCount(String redisKey, String type, BiFunction<Integer, Integer, Integer> updateFunction) {
        int processedCount = 0;
        try (Cursor<Map.Entry<Object, Object>> scan = redisTemplate.opsForHash().scan(redisKey, ScanOptions.NONE)) {
            while (scan.hasNext()) {
                Map.Entry<Object, Object> entry = scan.next();
                String key = (String) entry.getKey();
                String[] parts = key.split(":");
                int userId = Integer.parseInt(parts[1]);
                Integer count = (Integer) entry.getValue();
                if (updateFunction.apply(userId, count) > 0) {
                    processedCount++;
                }
            }
        } catch (Exception e) {
            log.error("同步{}时发生错误", type, e);
        }
        log.info("同步完成，处理了{}个{}", processedCount, type);
    }
}
