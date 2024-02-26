package com.xuecheng;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * ClassName: TeachPlanMapperTests
 * Package: com.xuecheng.content.config
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/19 21:05
 * @Version 1.0
 */
@SpringBootTest
public class TeachPlanMapperTests {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public void testTeachplanMapper(){
        /*List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(6L);
        System.out.println(teachplanDtos);*/
        System.out.println(teachplanMapper);
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);


    }
}
