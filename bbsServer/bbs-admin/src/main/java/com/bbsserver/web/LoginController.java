package com.bbsserver.web;


import com.alibaba.fastjson.JSONObject;
import com.bbsserver.common.consts.CommonConstant;
import com.bbsserver.common.dto.LoginDTO;
import com.bbsserver.common.vo.DataResult;
import com.bbsserver.common.vo.LoginVo;
import com.bbsserver.service.LoginService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public DataResult login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        //登录，登录失败会在直接抛出异常，进入异常处理流程中，不继续往下执行
        LoginVo login = loginService.login(loginDTO);
        //将登录成功后获得的loginVo对象写入session
        session.setAttribute(CommonConstant.SESSION_USER, JSONObject.toJSONString(login));
        //返回登录成功
        return DataResult.success(login);
    }

}
