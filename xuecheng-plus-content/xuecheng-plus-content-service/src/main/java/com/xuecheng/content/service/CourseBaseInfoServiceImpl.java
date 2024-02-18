package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: CourseBaseInfoServiceImpl
 * Package: com.xuecheng.content.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/16 15:35
 * @Version 1.0
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService{
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //封装page
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //封装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName())
                .eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus())
                .eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        //进行分页查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page,queryWrapper);
        //获取响应体中需要封装的属性
        List<CourseBase> items = courseBasePage.getRecords();
        Long total = courseBasePage.getTotal();
        //封装响应体
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, courseBasePage.getCurrent(), courseBasePage.getSize());

        System.out.println(courseBasePageResult);
        return courseBasePageResult;
    }
}
