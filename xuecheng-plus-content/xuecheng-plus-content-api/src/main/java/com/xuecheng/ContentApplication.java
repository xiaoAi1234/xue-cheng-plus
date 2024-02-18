package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ClassName: ContentApplication
 * Package: com.xuecheng.content.api
 * Description: 内容管理服务启动类
 *
 * @Author 艾子睿
 * @Create 2024/2/15 15:14
 * @Version 1.0
 */
@EnableSwagger2Doc
@SpringBootApplication
@MapperScan("com.xuecheng.content.mapper")
public class ContentApplication {
    public static void main(String[] args) {

        SpringApplication.run(ContentApplication.class, args);
    }
}
