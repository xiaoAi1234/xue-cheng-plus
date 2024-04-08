package com.xuecheng.ucenter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ClassName: PasswordAuthServiceImpl
 * Package: com.xuecheng.ucenter.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/4/3 9:18
 * @Version 1.0
 */
@Service("password_authservice")
public class PaswordAuthServiceImpl implements AuthService{
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //校验验证码
        //账号是否存在

        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(xcUser == null) {
            throw new RuntimeException("账号不存在");
        }
        //密码是否正确
        String passwordInput = authParamsDto.getPassword();
        String passwordDb = xcUser.getPassword();
        boolean matches = passwordEncoder.matches(passwordInput, passwordDb);
        if (!matches) {
            throw new RuntimeException("密码错误");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);

        return xcUserExt;
    }
}
