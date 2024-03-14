package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: MediaFileProcessServiceImpl
 * Package: com.xuecheng.media.service.impl
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/14 22:17
 * @Version 1.0
 *
 *
 */

@Service
@Slf4j
public class MediaFileProcessServiceImpl implements MediaFileProcessService {
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;


    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    @Override
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result<=0?false:true;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //判断该任务是否在待处理表中存在
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            return;
        }

        //若更新失败：更新待处理表
        MediaProcess newMediaProcess = new MediaProcess();
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<>();
        queryWrapperById.eq(MediaProcess::getId, taskId);
        if (status.equals("3")) {
            newMediaProcess.setStatus(status);
            newMediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            newMediaProcess.setErrormsg(errorMsg);
            //只更新newMediaProcess中与原记录属性不同的地方,效率更高
            mediaProcessMapper.update(newMediaProcess,queryWrapperById);
            log.debug("更新任务处理状态为失败，任务信息:{}",newMediaProcess);
            return ;

        }

        //任务成功
        //更新媒资文件表的url
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles != null) {
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }

        //更预发布表的url，状态，完成处理时间
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);

        //将原记录从预发布表中删去加入历史表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        mediaProcessMapper.deleteById(mediaProcess.getId());


    }

}
