package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * ClassName: CoursePublishService
 * Package: com.xuecheng.content.service
 * Description:课程发布
 *
 * @Author 艾子睿
 * @Create 2024/3/12 17:05
 * @Version 1.0
 */
@Service
public interface CoursePublishService {
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
    public void commitAudit(Long companyId,Long courseId);
    public void publish(Long companyId, Long courseId);
    public File generateCourseHtml(Long courseId);
    public void  uploadCourseHtml(Long courseId,File file);


}
