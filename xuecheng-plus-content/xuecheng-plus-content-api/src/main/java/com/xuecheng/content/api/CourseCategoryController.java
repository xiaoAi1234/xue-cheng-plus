package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: CourseCategoryController
 * Package: com.xuecheng.content.api
 * Description: 课程分类
 *
 * @Author 艾子睿
 * @Create 2024/2/18 19:31
 * @Version 1.0
 */
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {

        return courseCategoryService.queryTreeNodes("1");
    }

}
