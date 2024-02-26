package com.xuecheng;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ClassName: CourseCategoryMapperTests
 * Package: com.xuecheng.content.config
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/18 20:46
 * @Version 1.0
 */
@SpringBootTest
public class CourseCategoryMapperTests {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testCourseCategoryMapper () {
        System.out.println(courseCategoryMapper);
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);

    }
}
