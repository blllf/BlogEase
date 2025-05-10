package com.blllf.blogease;

import com.blllf.blogease.config.RestHighLevelClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableConfigurationProperties(RestHighLevelClientConfig.class)
@EnableAsync       // 启用异步支持
@EnableScheduling  // 启用定时任务
@SpringBootApplication
public class BlogEaseApplication {
    public static void main(String[] args) {
        ConfigurableEnvironment env = SpringApplication.run(BlogEaseApplication.class, args).getEnvironment();
        log.info("\n----------------------------------------------------------------------------------------\n\t" +
                        "Application: '{}' is running Success! \n\t" +
                        "Local URL: \thttp://localhost:{}\n\t" +
                        "Knife4j:\thttp://localhost:{}/doc.html\n" +
                        "----------------------------------------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.port"));

    }
}
