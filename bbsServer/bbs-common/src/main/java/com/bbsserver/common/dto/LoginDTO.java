package com.bbsserver.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//用于表示前端的登录表单
@Data
public class LoginDTO {

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String codeId;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
