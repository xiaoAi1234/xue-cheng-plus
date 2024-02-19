package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: GlobalExceptionHandler
 * Package: com.xuecheng.base.exception
 * Description:
 *
 * @Author 艾子睿
 * @Create 2024/2/19 15:46
 * @Version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理自定义异常
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {
        //记录异常
        log.error("系统异常{}", e.getErrMessage(), e);
        //解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
    //处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        //拿到异常信息提示
        //异常信息列表
        List<String> exceptions = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getFieldErrors().stream().forEach(item -> {
            exceptions.add(item.getDefaultMessage());
        });
        //分隔异常信息
        String errMessage = StringUtils.join(exceptions,',');

        //记录异常
        log.error("系统异常{}", errMessage);
        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
    //其他
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        //记录异常
        log.error("系统异常{}", e.getMessage(), e);
        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }
}
