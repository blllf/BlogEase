package com.blllf.blogease.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    //mybatis-plus 分页插件
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        return new PaginationInnerInterceptor();
    }


    //逻辑删除组件
    /*public ISqlInjector sqlInjector(){
        return (ISqlInjector) new LoginInterceptor();
    }*/
}
