package com.bbsserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {

    /**
     * 账号
     */
    @NotBlank(message ="账号不能为空")
    private String account;

    /**
     * 名称
     */
    @NotBlank(message ="名称不能为空")
    private String name;

    /**
     * 密码
     */
    @NotBlank(message ="密码不能为空")
    private String password;
    @NotBlank(message ="重复密码不能为空")
    private String rePassword;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String mail;

    private String headPath =  "noHead";
}

