package com.xuecheng.ucenter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import org.apache.commons.lang.StringUtils;
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
public class PasswordAuthServiceImpl implements AuthService{
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CheckCodeClient checkCodeClient;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //远程调用校验验证码服务的接口，实现验证码的校验
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (StringUtils.isEmpty(checkcodekey) || StringUtils.isEmpty(checkcode)) {
            throw new RuntimeException("输入验证码为空");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify == null || !verify) {
            //这里需要先判空，因为当调用降级方法时verify为空
            throw new RuntimeException("验证码输入错误");
        }



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
            //若密码错误则抛出异常
            throw new RuntimeException("密码错误");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);

        return xcUserExt;
    }
}
