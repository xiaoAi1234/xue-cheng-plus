package com.xuecheng.base.exception;

/**
 * ClassName: XueChengPlusException
 * Package: com.xuecheng.base.exception
 * Description: 自定义异常处理
 *
 * @Author 艾子睿
 * @Create 2024/2/19 15:15
 * @Version 1.0
 */
public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }
    //常见异常
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    //自定义异常内容提示
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }

}
