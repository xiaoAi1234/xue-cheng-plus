package com.xuecheng.media.api;

import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * ClassName: BigFilesController
 * Package: com.xuecheng.media.api
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/29 20:09
 * @Version 1.0
 */
@RestController
public class BigFilesController {
    @Autowired
    MediaFileService mediaFileService;



    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(
            @RequestParam("fileMd5") String fileMd5
    ) throws Exception {
        return mediaFileService.checkFile(fileMd5);
    }


    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5,chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        Long companyId = 1232141425L;
        //文件路径
        File tempFile = File.createTempFile("minio", "temp");
        file.transferTo(tempFile);
        String absolutePath = tempFile.getAbsolutePath();
        //上传文件

        return mediaFileService.uploadChunk(fileMd5,chunk,absolutePath);

    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        return null;

    }

}
