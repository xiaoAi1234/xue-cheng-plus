package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName: EditCourseDto
 * Package: com.xuecheng.content.model.dto
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/19 17:23
 * @Version 1.0
 */
@Data
public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程Id", required = true)
    private Long id;
}
