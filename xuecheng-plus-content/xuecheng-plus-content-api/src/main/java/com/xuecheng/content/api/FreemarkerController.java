package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName: FreemarkerController
 * Package: com.xuecheng.content.api
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/1 19:44
 * @Version 1.0
 */
//返回的不再是json了
@Controller
public class FreemarkerController {

    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("name","小明");
        //设置模板名称
        modelAndView.setViewName("test");
        return modelAndView;
    }


}

