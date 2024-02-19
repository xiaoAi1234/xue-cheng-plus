package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

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

    @Override
    @Transactional
    //增删改事务处理
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        /*//合法性校验,可能存在直接调service的情况
        if (StringUtils.isBlank(dto.getName())) {
            //使用自定义异常
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }*/

        //向课程基本信息表插数据
        CourseBase courseBaseNew = new CourseBase();
        //将dto中有的与courseBase属性对应的信息拷贝到courseBase
        //若为空则courseBase中也为空
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置默认信息以及从页面传递来的信息
        courseBaseNew.setAuditStatus("202002");
        courseBaseNew.setStatus("203001");
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入，在插入后courseBaseNew就有了id
        int num = courseBaseMapper.insert(courseBaseNew);
        if (num <= 0) {
            //一定要用RuntimeException
            throw new RuntimeException("课程新增失败！");
        }

        //向课程营销信息表插数据
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        //课程营销信息表与课程基础信息表对应
        courseMarketNew.setId(courseBaseNew.getId());
        //插入
        int i = saveCourseMarket(courseMarketNew);
        if(i<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }


        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseBaseNew.getId());

    }

    private int saveCourseMarket(CourseMarket courseMarketNew) {
        //参数合法性检验
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }
        //不存在就插入否则更新
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarketObj == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else{
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }


    }

    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        //营销信息若有就加上但基本信息一定要有
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }

    @Override
    //修改课程
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //修改课程基本信息
        //进行非一般的参数校验，本机构只能修改本机构的课程
        CourseBase courseBase = courseBaseMapper.selectById(editCourseDto.getId());
        //若为空，抛异常
        if (courseBase == null) {
            XueChengPlusException.cast("该课程不存在");
        }
        if (companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        BeanUtils.copyProperties(editCourseDto,courseBase);
        //补充信息
        courseBase.setChangeDate(LocalDateTime.now());

        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0) {
            XueChengPlusException.cast("修改课程失败");
        }

        //修改课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        saveCourseMarket(courseMarket);
        return getCourseBaseInfo(courseBase.getId());
    }


}
