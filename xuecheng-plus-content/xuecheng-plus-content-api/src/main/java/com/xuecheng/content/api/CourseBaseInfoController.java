package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: CourseBaseInfoController
 * Package: com.xuecheng.content.api
 * Description: 课程基础信息控制类
 *
 * @Author 艾子睿
 * @Create 2024/2/15 14:49
 * @Version 1.0
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        //造假数据用于测试
        /*
        //造查询到的对象
        CourseBase courseBase = new CourseBase();
        courseBase.setName("测试名称");
        courseBase.setCreateDate(LocalDateTime.now());
        //造查询到的表
        List<CourseBase> courseBases = new ArrayList();
        courseBases.add(courseBase);
        //造返回结果
        PageResult pageResult = new PageResult<CourseBase>(courseBases, 10, 1, 10);
        */
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println(pageResult);
        return pageResult;


    }

    @ApiOperation("课程添加接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto) {
        Long companyId = 1232141425L;
        return courseBaseInfoService.createCourseBase(companyId,addCourseDto);

    }

    @ApiOperation("课程添加接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        return courseBaseInfo;

    }

    @ApiOperation("课程修改接口")
    @PutMapping ("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class)EditCourseDto editCourseDto) {
       return null;

    }
}
