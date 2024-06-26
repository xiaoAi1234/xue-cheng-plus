package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ClassName: CheckCodeClientFactory
 * Package: com.xuecheng.ucenter.feignclient
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/4/29 15:22
 * @Version 1.0
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {

            @Override
            public Boolean verify(String key, String code) {
                log.debug("调用验证码服务熔断异常:{}", throwable.getMessage());
                System.out.println("调用验证码服务熔断异常:{}");
                return null;
            }
        };
    }
}

