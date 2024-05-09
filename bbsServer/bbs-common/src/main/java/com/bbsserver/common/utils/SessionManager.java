package com.bbsserver.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.bbsserver.common.consts.CommonConstant;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.vo.LoginVo;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

public class SessionManager {
    //从session中获取用户信息，即其登录时存储于session中并返回的登录信息
    public static LoginVo getUser() {
        Object attribute = Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(CommonConstant.SESSION_USER, RequestAttributes.SCOPE_SESSION);
        if (null == attribute) {
            throw new CommonException("未找到用户");
        }
        return JSONObject.parseObject(attribute.toString(), LoginVo.class);
    }
}
