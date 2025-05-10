package com.blllf.blogease.task;

import com.blllf.blogease.service.impl.RedisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class RedisAndMysqlTask extends QuartzJobBean {

    @Autowired
    private RedisServiceImpl redisService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        //3. mysql中用户数据读取到 redis中
        redisService.RedForMysLike();

        //4.mysql中点赞数量数据读取到 redis中
        redisService.RedCountForMysLike();

        //1. redis用户 数据保存到本地
        redisService.addToLocalDB();

        //2. redis点赞数量数据保存到本地
        redisService.addCountToLocalDB();

    }
}
