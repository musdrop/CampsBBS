package com.bbsserver.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String account;

    private String name;

    private String password;

    private String mail;

    @TableField(value = "head")
    private String headPath;

    //为0时是普通用户，为1时是管理员
    private Integer auth;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;
}
