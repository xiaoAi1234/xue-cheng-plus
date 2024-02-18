package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * ClassName: CourseBaseInfoService
 * Package: com.xuecheng.content.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/16 15:29
 * @Version 1.0
 */
public interface CourseBaseInfoService {
    //课程分页查询
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
}
