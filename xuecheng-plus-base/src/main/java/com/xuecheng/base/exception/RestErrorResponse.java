package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * ClassName: RestErroResponse
 * Package: com.xuecheng.base.exception
 * Description: 响应用户的统一参数
 *
 * @Author 艾子睿
 * @Create 2024/2/19 15:11
 * @Version 1.0
 */
public class RestErrorResponse implements Serializable {
    String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }


}
