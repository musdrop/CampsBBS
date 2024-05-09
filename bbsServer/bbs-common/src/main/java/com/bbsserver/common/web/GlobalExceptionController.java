package com.bbsserver.common.web;

import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.vo.DataResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/*全局异常捕获
 用于处理Controller抛出的异常，保证发生异常了也至少有返回给前端的值，错误码
*/
@RestControllerAdvice
@Log4j2
public class GlobalExceptionController {
    //自定义异常
    @ExceptionHandler(CommonException.class)
    public DataResult noStackException(final CommonException e) {
        log.error("exception,msg:{}",e.getMessage());
        return DataResult.fail(e.getMessage());
    }
    //参数校验的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResult noStackException(final MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidException,msg:{}",e.getMessage());
        String errorMsg = e.getAllErrors().get(0).getDefaultMessage();
        return DataResult.fail(errorMsg);
    }
    //未知异常
    @ExceptionHandler(Exception.class)
    public DataResult noStackException(final Exception e) {
        log.error("exception,",e);
        return DataResult.fail("未知异常");
    }
}
