package com.xuecheng;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: FreemarkerTest
 * Package: com.xuecheng
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/3/19 15:25
 * @Version 1.0
 */
@SpringBootTest
public class FreemarkerTest {

    @Autowired
    CoursePublishService coursePublishService;


    //测试页面静态化
    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        //拿到模版
        String classpath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("course_template.ftl");
        //拿到数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(1L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewInfo);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\DailyLife\\work\\projects\\xuecheng\\StaticHtmlOutput\\1.html"));
        IOUtils.copy(inputStream,outputStream);


    }

}

