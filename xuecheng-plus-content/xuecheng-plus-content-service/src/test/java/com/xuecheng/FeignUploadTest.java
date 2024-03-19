package com.xuecheng;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * ClassName: FeignUploadTest
 * Package: com.xuecheng
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/19 16:43
 * @Version 1.0
 */
@SpringBootTest
public class FeignUploadTest {

    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() throws IOException {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\DailyLife\\work\\projects\\xuecheng\\StaticHtmlOutput\\1.html"));
        String upload = mediaServiceClient.upload(multipartFile, "course", "test.html");
        if (upload == null) {
            System.out.println("走了降级逻辑");
        }
    }

}

