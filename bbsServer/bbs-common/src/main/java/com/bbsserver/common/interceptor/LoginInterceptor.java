package com.bbsserver.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.bbsserver.common.consts.CommonConstant;
import com.bbsserver.common.vo.DataResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

//登录拦截器
public class LoginInterceptor implements HandlerInterceptor {

    //控制器处理请求前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从Http请求里获取session
        HttpSession session = request.getSession();
        //如果没有user属性，说明未登录
        if (session.getAttribute(CommonConstant.SESSION_USER) == null) {
            //设置返回内容的格式为json
            response.setContentType("application/json;charset=utf-8");
            //返回里的data里写入未登录的json提示信息，包含错误码和消息
            response.getWriter().write(JSONObject.toJSONString(DataResult.fail(999,"用户未登录")));
            //拦截，不往下走了
            return false;
        }
        //继续
        return true;
    }
}
