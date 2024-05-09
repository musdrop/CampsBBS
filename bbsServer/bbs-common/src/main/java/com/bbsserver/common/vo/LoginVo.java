package com.bbsserver.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginVo {

    private Integer id;

    /**
     * 账号
     */
    private String account;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 头像
     */
    private String head;

    /**
     * 权限等级，0用户，1管理员
     */
    private Integer authLevel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
