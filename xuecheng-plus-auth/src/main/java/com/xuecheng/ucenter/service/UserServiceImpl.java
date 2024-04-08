package com.xuecheng.ucenter.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * ClassName: UserServiceImpl
 * Package: com.xuecheng.ucenter.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/4/2 22:42
 * @Version 1.0
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;
    //注入spring容器
    @Autowired
    ApplicationContext applicationContext;
    @Override
    //AuthParamsDto传入此处
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json串转成AuthParamsDto
        AuthParamsDto authParamsDto = null;
        //防止传入的json串与对象不匹配
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("传入的json串与对象不匹配");
        }
        //根据认证类型从容器中取相应的Bean
        String authType = authParamsDto.getAuthType();
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //完成认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装xcUserExt为userDetails
        UserDetails userPrincipal = getUserPrincipal(xcUserExt);
        return userPrincipal;
    }

    public UserDetails getUserPrincipal(XcUserExt user){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};
        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password ).authorities(authorities).build();
        return userDetails;
    }

}
