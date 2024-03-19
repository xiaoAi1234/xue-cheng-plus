package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ClassName: MediaServiceClient
 * Package: com.xuecheng.content.feignclient
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/19 16:35
 * @Version 1.0
 */
//使用fallback无法拿到熔断异常，使用fallbackFactory可以
@FeignClient(value = "media-api", configuration = {MultipartSupportConfig.class}, fallbackFactory = MediaServiceClientFallBackFactory.class)
public interface MediaServiceClient {
    @ApiOperation("上传文件")
    @RequestMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public String upload(@RequestPart("filedata") MultipartFile filedata, @RequestParam(value = "folder",required=false) String folder, @RequestParam(value = "objectName",required=false) String objectName) throws IOException ;
}
