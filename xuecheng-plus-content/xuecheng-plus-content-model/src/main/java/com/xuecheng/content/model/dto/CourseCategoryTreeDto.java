package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * ClassName: CourseCategoryTreeDto
 * Package: com.xuecheng.content.model.dto
 * Description: 课程分类树模型类
 *
 * @Author 艾子睿
 * @Create 2024/2/18 19:28
 * @Version 1.0
 */
@Data
//实现serializable便于网络传输
public class CourseCategoryTreeDto extends CourseCategory implements java.io.Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
