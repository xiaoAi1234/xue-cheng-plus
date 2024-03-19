package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: CoursePublishTask
 * Package: com.xuecheng.content.service.jobhandler
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/19 14:16
 * @Version 1.0
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        log.debug("保存课程索引信息,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mq拿课程id
        long id = Long.parseLong(mqMessage.getBusinessKey1());
        //上传静态页面到minIO
        generateCourseHtml(id,mqMessage);
        //将课程信息缓存至redis
        saveCourseCache(mqMessage,id);
        //保存课程索引信息
        saveCourseIndex(mqMessage,id);

        return true;
    }

    private void generateCourseHtml(Long courseId, MqMessage mqMessage) {
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //为了实现任务的幂等性，通过taskId找第一阶段状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("页面静态化已完成");
        }

        //执行任务
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            XueChengPlusException.cast("生成的静态页面为空");
        }
        coursePublishService.uploadCourseHtml(courseId,file);


        //任务已完成
        mqMessageService.completedStageOne(taskId);


    }
}
