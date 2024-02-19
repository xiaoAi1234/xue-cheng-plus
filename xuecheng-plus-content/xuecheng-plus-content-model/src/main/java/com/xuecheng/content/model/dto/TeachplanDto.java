package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * ClassName: TeachPlanDto
 * Package: com.xuecheng.content.model.dto
 * Description: 课程计划
 *
 * @Author 艾子睿
 * @Create 2024/2/19 19:42
 * @Version 1.0
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;
    //子结点
    List<TeachplanDto> teachPlanTreeNodes;

}
