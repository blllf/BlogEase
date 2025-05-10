package com.blllf.blogease.config;

import com.blllf.blogease.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
* @Configuration
* @Configuration 中所有带 @Bean 注解的方法都会被 CGLIB 动态代理，调用这些配置类中的方法都会返回同一个 Bean 实例（默认行为，可以通过 proxyBeanMethods 属性进行配置）。
* 相比之下，@Component 注解的类不会被代理，每次调用配置类中的方法都会新建一个 Bean 实例。
*
*
* */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录接口和注册接口放行
        //把拦截器注册进去
        registry.addInterceptor(loginInterceptor).excludePathPatterns( "/user/login","/user/register","/user/sendEmail" ,
                "/user/findPwd" ,"/fileExport" ,"/user/validateRememberMe" , "/system/metrics", "/comment/findAllComments",

                // 添加Knife4j相关路径
                "/doc.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-resources");

    }



}
