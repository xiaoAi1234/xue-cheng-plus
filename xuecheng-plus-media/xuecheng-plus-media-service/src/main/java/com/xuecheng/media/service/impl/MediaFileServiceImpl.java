package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    private MinioClient minioClient;
    //只有被注入的才是代理类，在一个类的方法中调用自己的另一个方法，另一个方法的事务不会生效
    @Autowired
    MediaFileService currentProxy;
    @Value("${minio.bucket.videofiles}")
    private String bucket_videofiles;
    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    //获取类型名
    //抽取出来是因为很多其他方法要用
    private String getMimeType(String extension) {
        //防止报空指针异常
        if (extension == null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    //获取默认文件路径
    private String getDefaultFolderPath(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String path = sdf.format(new Date()).replace('-', '/') + '/';
        return path;
    }

    //MD5加密后获取对象名
    private String getFileMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            String s = DigestUtils.md5Hex(fileInputStream);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    //把媒体资源传给MinIO
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("文件上到Minio，bucket:{}, objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件上传出错，bucket:{}, objectName:{}, 错误信息:{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    //添加文件信息到数据库，如果文件已存在则不进行上传
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
                XueChengPlusException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());

        }
        return mediaFiles;

    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();

            //拿到输入流
            GetObjectArgs getObjectArgs = GetObjectArgs.
                    builder().
                    bucket(bucket).
                    object(filePath).
                    build();
            InputStream inputStream = null;

            try {
                inputStream = minioClient.getObject(getObjectArgs);
                if (inputStream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //要进行流关闭
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        return RestResponse.success(false);
    }


    //上传文件并返回信息给Service
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        //拿到拓展名
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        //拿到mimeType
        String mimeType = getMimeType(extension);
        //拿到对象名
        File file = new File(localFilePath);
        String defaultFolderPath = getDefaultFolderPath(file);
        String fileMD5 = getFileMD5(file);
        String objectName = defaultFolderPath + fileMD5;
        //把文件传给minio
        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_files, objectName);
        if (!result) {
            XueChengPlusException.cast("上传文件失败");
        }
        //将文件信息存储到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMD5, uploadFileParamsDto, bucket_files, objectName);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;

    }


    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videofiles)
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        } finally {
            //要进行流关闭
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //分块未存在
        return RestResponse.success(false);
    }

    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localTrunchunkFilePath) {


        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        //拿媒体类型
        String mimeType = getMimeType(null);

        try {
            //将文件存储至minIO
            addMediaFilesToMinIO(localTrunchunkFilePath, mimeType, bucket_videofiles, chunkFilePath);
            return RestResponse.success(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("上传分块文件:{},失败:{}",chunkFilePath,ex.getMessage());
        }
        return RestResponse.validfail(false,"上传分块失败");
    }


}
