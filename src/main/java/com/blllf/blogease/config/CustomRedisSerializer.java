package com.blllf.blogease.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

//自定义序列化器
public class CustomRedisSerializer extends Jackson2JsonRedisSerializer<Object> {
    public CustomRedisSerializer(Class<Object> clazz) {
        super(clazz);
        ObjectMapper objectMapper = new ObjectMapper();
        // 确保所有的属性都能被序列化/反序列化
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        // 启用默认类型以支持多态类型处理
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 设置ObjectMapper
        this.setObjectMapper(objectMapper);
    }
}
