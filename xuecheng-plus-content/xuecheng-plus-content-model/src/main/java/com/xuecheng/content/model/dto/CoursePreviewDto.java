package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * ClassName: CoursePreviewDto
 * Package: com.xuecheng.content.model.dto
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/12 17:02
 * @Version 1.0
 */
@Data
@ToString
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;
    //课程计划信息
    List<TeachplanDto> teachplans;

}
