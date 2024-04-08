package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import org.springframework.stereotype.Service;

/**
 * ClassName: WxAuthServiceImpl
 * Package: com.xuecheng.ucenter.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/4/3 9:20
 * @Version 1.0
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService{
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
