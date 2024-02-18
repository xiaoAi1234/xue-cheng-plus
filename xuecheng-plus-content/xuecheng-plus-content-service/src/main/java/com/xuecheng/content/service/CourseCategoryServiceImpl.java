package com.xuecheng.content.service;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: CourseCategoryServiceImpl
 * Package: com.xuecheng.content.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/18 21:17
 * @Version 1.0
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService{
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //将从数据库中查到的list转换为map,同时过滤掉根结点
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        Map<String, CourseCategoryTreeDto> categoryTreeDtoMap = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(key -> key.getId(), value -> value,(key1, key2) -> key2));
        //创建结果集
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();
        //遍历查到的list
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    if (id.equals(item.getParentid())) {
                        //将父节点加入结果集中
                        courseCategoryTreeDtoList.add(item);
                    }

                    //获取当前item的父节点
                    CourseCategoryTreeDto parentDto = categoryTreeDtoMap.get(item.getParentid());

                    if (parentDto != null) {
                        if (parentDto.getChildrenTreeNodes() == null) {
                            parentDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }

                        parentDto.getChildrenTreeNodes().add(item);
                    }

                });

        return courseCategoryTreeDtoList;
    }
}
