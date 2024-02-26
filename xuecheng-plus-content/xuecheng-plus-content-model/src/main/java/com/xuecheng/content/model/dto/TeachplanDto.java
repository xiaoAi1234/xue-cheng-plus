package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * ClassName: TeachplanDto
 * Package: com.xuecheng.content.model.dto
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/22 10:54
 * @Version 1.0
 */
@Data
public class TeachplanDto extends Teachplan {
    // 课程计划关联的媒资信息,章节没有关联的媒资信息
    private TeachplanMedia teachplanMedia;
    // 章节下的小节信息
    private List<TeachplanDto> teachPlanTreeNodes;
}

