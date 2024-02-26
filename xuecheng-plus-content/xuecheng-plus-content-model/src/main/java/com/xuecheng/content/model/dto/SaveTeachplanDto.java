package com.xuecheng.content.model.dto;

import com.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * ClassName: SaveTeachplanDto
 * Package: com.xuecheng.content.model.dto
 * Description: 新增大章节、小章节，修改章节的前端传递信息
 *
 * @Author 艾子睿
 * @Create 2024/2/26 21:44
 * @Version 1.0
 */
@Data
public class SaveTeachplanDto {
    /***
     * 教学计划id
     */
    private Long id;

    /**
     * 课程计划名称
     */
    @NotEmpty(groups = {ValidationGroups.Inster.class}, message = "插入课程名称不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class}, message = "修改课程名称不能为空")
    @ApiModelProperty(value = "课程名称", required = true)
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;


}
