package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * ClassName: QueryCourseParamsDto
 * Package: com.xuecheng.content.model.dto
 * Description: 课程查询条件模型类
 *
 * @Author 艾子睿
 * @Create 2024/2/15 14:35
 * @Version 1.0
 */
@Data
@ToString
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}

