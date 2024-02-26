package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TeachplanServiceImpl
 * Package: com.xuecheng.content.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/23 10:50
 * @Version 1.0
 */
@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachPlan(SaveTeachplanDto teachplanDto) {
        Long teachplanId = teachplanDto.getId();
        if (teachplanId == null) {
            //插入

            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            //查出与新插入节同级的节点的总数，设置新节点的orderby字段
            Long courseId = teachplan.getCourseId();
            Long parentid = teachplan.getParentid();
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId,courseId).eq(Teachplan::getParentid,parentid);
            Integer integer = teachplanMapper.selectCount(queryWrapper);
            teachplan.setOrderby(integer + 1);
            //若添加的为一大节需设置setIsPreview字段，因为前端并未传该字段
            teachplan.setIsPreview("0");
            teachplanMapper.insert(teachplan);


        } else {
            //修改

            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);

        }
    }
}
