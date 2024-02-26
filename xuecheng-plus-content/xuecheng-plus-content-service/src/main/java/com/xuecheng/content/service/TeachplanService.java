package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TeachplanService
 * Package: com.xuecheng.content.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/23 10:49
 * @Version 1.0
 */
public interface TeachplanService {
    public List<TeachplanDto> findTeachplanTree(long courseId);
    public void saveTeachPlan(SaveTeachplanDto teachplanDto);

    }
