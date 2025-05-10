package com.blllf.blogease.config;


import com.blllf.blogease.task.RedisAndMysqlTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final String REDIS_AND_MYSQL = "RedisTask";

    @Bean
    public JobDetail quartzDetail(){
        return JobBuilder.newJob(RedisAndMysqlTask.class).withIdentity(REDIS_AND_MYSQL).storeDurably().build();
    }

    @Bean
    public Trigger quartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(2) //设置时间周期单位 2h
                //.withIntervalInSeconds(30)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(quartzDetail())
                .withIdentity(REDIS_AND_MYSQL)       //设置触发器的标识
                .withSchedule(scheduleBuilder)
                .build();
    }
}
