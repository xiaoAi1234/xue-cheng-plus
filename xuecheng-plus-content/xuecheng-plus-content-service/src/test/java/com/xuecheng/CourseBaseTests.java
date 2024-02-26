package com.xuecheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ClassName: CourseBaseTests
 * Package: com.xuecheng
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/15 20:05
 * @Version 1.0
 */
@SpringBootTest
public class CourseBaseTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Test
    public void testCourseBaseTests (){
        /*
        CourseBase courseBase = courseBaseMapper.selectById(18);
        //断言不为空
        Assertions.assertNotNull(courseBase);
         */

        /*分页查询测试*/

        //造请求对象
        System.out.println(courseBaseMapper);
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        Page<CourseBase> page = new Page<>(1L,2L);
        //确定查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        //进行查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        //获取查询到的列表、总记录数
        List<CourseBase> items = courseBasePage.getRecords();
        long total = courseBasePage.getTotal();
        //封装响应体
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, courseBasePage.getCurrent(), courseBasePage.getSize());

        //输出
        System.out.println(courseBasePageResult);




    }
}
