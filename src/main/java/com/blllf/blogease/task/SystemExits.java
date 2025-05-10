package com.blllf.blogease.task;

import com.blllf.blogease.service.impl.RedisServiceImpl;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemExits {

    @Autowired
    private RedisServiceImpl redisService;

    //@PreDestroy注解的方法，当Spring容器关闭时，这个方法会被自动调用。
    //系统关闭后 把redis 中的数据读取到MySQL中， 并且清除redis中的数据
    @PreDestroy
    public void executeRedis2MySql(){

        //1. redis用户 数据保存到本地
        redisService.addToLocalDB2();

        //2. redis点赞数量数据保存到本地
        redisService.addCountToLocalDB2();
    }
}
