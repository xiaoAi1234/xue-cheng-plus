package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * ClassName: AuthService
 * Package: com.xuecheng.ucenter.service
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/4/3 9:15
 * @Version 1.0
 */
public interface AuthService {
    XcUserExt execute(AuthParamsDto authParamsDto);
}
