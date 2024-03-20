package com.xuecheng.content.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import feign.template.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.lang.Long;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * ClassName: CoursePublishServiceImpl
 * Package: com.xuecheng.content.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/12 17:06
 * @Version 1.0
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachplanService teachplanService;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CoursePublishMapper coursePublishMapper;
    @Autowired
    MqMessageService mqMessageService;
    @Autowired
    MediaServiceClient mediaServiceClient;
    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);


        return coursePreviewDto;
    }
    @Override
    @Transactional
    public void publish(Long companyId, Long courseId){
        //查询课程发布表,查出状态为已发布状态的数据
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null) {
            XueChengPlusException.cast("请先提交课程审核，审核通过才可以发布");
        }
            String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            XueChengPlusException.cast("操作失败，课程审核通过方可发布。");
        }

        //向课程发布表写数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        if (coursePublishMapper.selectById(courseId) != null) {
            coursePublishMapper.updateById(coursePublish);
        } else {
            coursePublishMapper.insert(coursePublish);
        }

        //向消息表写数据
        MqMessage mqMessage = saveCoursePublishMessage(courseId);

        //删掉课程预发布表
        coursePublishPreMapper.deleteById(courseId);

    }

    @Override
    public File generateCourseHtml(Long courseId) {

        File htmlFile = null;

        try {
            Configuration configuration = new Configuration(Configuration.getVersion());
            //拿到模版
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            configuration.setDefaultEncoding("utf-8");
            Template template = configuration.getTemplate("course_template.ftl");
            //拿到数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);
            String html = processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            htmlFile = File.createTempFile("coursepublish", ".html");
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception ex) {
            log.error("静态化页面出现问题，课程号：{}", courseId);
            ex.printStackTrace();
        }


        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {

        try {
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            String upload = mediaServiceClient.upload(multipartFile, "course/", "/course/" + courseId + ".html");
            if (upload == null) {
                log.debug("走了降级逻辑");
                XueChengPlusException.cast("走了降级逻辑");
            }
        } catch (Exception ex) {
            XueChengPlusException.cast("走了降级逻辑");
        }


    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        //若redis有则从redis中拿
        Object jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
        if (jsonObj != null){
            String jsonString = jsonObj.toString();
            if ("null".equals(jsonString)){
                return null;
            }
            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
            return coursePublish;
        } else {
            //使用同步锁防止缓存击穿
            synchronized (this) {
                //再次查询缓存
                jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
                if (jsonObj != null) {
                    String jsonString = jsonObj.toString();
                    if ("null".equals(jsonString)) {
                        return null;
                    }
                    CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
                    return coursePublish;
                }
                //先从数据库中拿再存入redis
                CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                //if (coursePublish != null)
                //加入过期时间
                //过期时间随机防止缓存雪崩
                redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish), 30 + new Random().nextInt(100), TimeUnit.SECONDS);
                return coursePublish;
            }

        }

    }

    private MqMessage saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }

        return mqMessage;
    }


    @Transactional
    @Override
    public void commitAudit(Long companyId,Long courseId){
        //查课程的营销信息，课程计划，基本信息；将其插入课程预发布表；更新课程基本信息中的课程状态

        //查课程基本信息并进行约束，课程必须存在，状态为已提交，课程信息完整
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            XueChengPlusException.cast("该课程不存在");
        }
        if (courseBaseInfo.getAuditStatus().equals("202003")) {
            XueChengPlusException.cast("该课程已提交请等待审核");
        }
        if (StringUtils.isEmpty(courseBaseInfo.getPic())) {
            XueChengPlusException.cast("图片未上传，请上传图片");
        }

        //查询课程计划和营销信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.size() == 0) {
            XueChengPlusException.cast("请添加课程计划");
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //将相关信息填入预发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        String s = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(s);
        s = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(s);

        //补充必要信息如创建时间，状态
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setStatus("202002");

        //如果已有该记录则更新否则插入
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre1 == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            //可能是审核通过或未通过再次提交
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本表的审核状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);

    }
}
