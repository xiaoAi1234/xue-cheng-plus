package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * ClassName: MinioTest
 * Package: com.xuecheng.media
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/27 18:04
 * @Version 1.0
 */
public class MinioTest {
    @Test
    public void test_upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //凭证
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.101.65:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".png");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }

        //上传,记得做异常抛出
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("C:\\Users\\小灰灰\\Pictures\\壁纸\\韶颜倾城.png")
                .object("test/韶颜倾城")
                .contentType(mimeType)
                .build();

        minioClient.uploadObject(uploadObjectArgs);

    }

    @Test
    public void test_delete() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //凭证
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.101.65:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket("testbucket").filename("C:\\Users\\小灰灰\\Pictures\\壁纸\\韶颜倾城.png").object("韶颜倾城").build();
        RemoveObjectArgs build = RemoveObjectArgs.builder().bucket("testbucket").object("韶颜倾城").build();
        minioClient.removeObject(build);
    }

    @Test
    public void test_download() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //凭证
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.101.65:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        //拿到输入流
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("韶颜倾城").build();
         FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
         //指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("C:\\Users\\小灰灰\\Downloads\\shaoyan.png"));
        //apache下
        IOUtils.copy(inputStream,outputStream);

        //对文件内容进行md5来校验文件完整性，使用本地流如果使用网络流流本身可能不完整
        //若要校验上传的文件是否完整就把文件下到本地再与本地原文件进行比较
        String source_md5 = DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\小灰灰\\Pictures\\壁纸\\韶颜倾城.png")));
        String target_md5 = DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\小灰灰\\Downloads\\shaoyan.png")));
        if (source_md5.equals(target_md5)) {
            System.out.println("成功！！！");
        }
    }
}
