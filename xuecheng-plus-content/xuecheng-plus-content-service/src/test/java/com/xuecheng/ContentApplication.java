package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName: ContentApplication
 * Package: com.xuecheng.content.api
 * Description: 内容管理服务启动类
 *
 * @Author 艾子睿
 * @Create 2024/2/15 15:14
 * @Version 1.0
 */
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
