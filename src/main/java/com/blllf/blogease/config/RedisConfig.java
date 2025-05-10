package com.blllf.blogease.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 配置了它的序列化器和连接工厂
 * ObjectMapper 被配置并用于一个 Jackson2JsonRedisSerializer 的实例，这个序列化器随后可能被设置为 RedisTemplate 的一个值序列化器，将Java对象存储为JSON格式的字符串在Redis中
 * */

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用自定义的序列化器
        CustomRedisSerializer customRedisSerializer = new CustomRedisSerializer(Object.class);

        // 使用String序列化器进行key的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置key和hashKey的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // 设置value和hashValue的序列化方式
        template.setValueSerializer(customRedisSerializer);
        template.setHashValueSerializer(customRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
