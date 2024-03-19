package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Slf4j
@Component
public class VideoTask {
    private static Logger logger = LoggerFactory.getLogger(VideoTask.class);
    @Autowired
    MediaFileProcessService mediaFileProcessService;
    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;
    @Autowired
    MediaFileService mediaFileService;





    /**
     * 2、分片广播任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        //确定cpu核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
        //取到的任务数量
        int size = mediaProcessList.size();
        if (size <= 0) {
            log.debug("取到视频任务数" + size);
            return;
        }


        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(() -> {
               try{
                //开启任务并抢占
                Long id = mediaProcess.getId();
                boolean b = mediaFileProcessService.startTask(id);
                if (!b) {
                    log.debug("抢占任务失败，任务id:{}",id);
                }


                String bucket = mediaProcess.getBucket();
                //md5值
                String fileId = mediaProcess.getFileId();
                //文件路径
                String filePath = mediaProcess.getFilePath();
                //将视频从minio下载到本地的临时文件中
                File file = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                if (file == null) {
                    log.debug("下载视频失败，任务id:{}，桶：{}，文件名：{}",id, bucket, filePath);
                    mediaFileProcessService.saveProcessFinishStatus(id,"3",fileId,null,"下载视频失败到本地");
                }


                String ffmpeg_path = ffmpegpath;
                String video_path = file.getAbsolutePath();
                //创建一个临时文件用于存储转换后的文件
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("minio", ".mp4");
                } catch (IOException e) {
                    log.error("创建mp4临时文件失败");
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "创建mp4临时文件失败");
                    return;
                }


                String mp4_path = tempFile.getAbsolutePath();
                String mp4_name = tempFile.getName();
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4_path);
                //视频转换，成功将返回success
                String result = videoUtil.generateMp4();
                if (!result.equals("success")) {
                    //记录错误信息
                    log.error("视频转码失败,视频地址:{},错误信息:{}", bucket + filePath, result);
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "视频转码失败");
                    return;
                }

                //将转换后的视频上传minIO
                   String newObjectName = filePath.substring(0,filePath.lastIndexOf('.')) + ".mp4";
                   boolean isAdd = mediaFileService.addMediaFilesToMinIO(mp4_path, "video/mp4", bucket, newObjectName);
                if (!isAdd) {
                    //记录错误信息
                    log.error("上传视频失败");
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "上传视频失败");
                    return;
                }

                //保存任务处理成功结果
                String url = mediaFileService.getFilePathByMd5(fileId, ".mp4");
                mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2", fileId, url, null);} finally {
                   countDownLatch.countDown();
               }


            });

        });
        //最多等待30min后就解除阻塞，防止断电等情况
        countDownLatch.await(30, TimeUnit.MINUTES);


    }


}
