package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ClassName: MediaServiceClientFallBackFactory
 * Package: com.xuecheng.content
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/19 19:15
 * @Version 1.0
 */
@Slf4j
@Component
public class MediaServiceClientFallBackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {

        return new MediaServiceClient() {
            //当发生熔断，上级会调用此方法来执行降级的逻辑
            @Override
            public String upload(MultipartFile filedata, String folder, String objectName) throws IOException {
                log.debug("上传文件接口发生熔断：{}", throwable.toString(),throwable);
                return null;
            }
        };
    }
}
