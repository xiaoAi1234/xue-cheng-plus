package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ClassName: MediaServiceClientFallBack
 * Package: com.xuecheng.content.feignclient
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/19 19:11
 * @Version 1.0
 */
public class MediaServiceClientFallBack implements MediaServiceClient{
    @Override
    public String upload(MultipartFile filedata, String folder, String objectName) throws IOException {
        return null;
    }
}
