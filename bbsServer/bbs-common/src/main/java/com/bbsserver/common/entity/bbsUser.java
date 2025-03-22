package com.bbsserver.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class bbsUser {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String account;

    private String name;

    private String password;

    private String mail;

    private String head;

    //为0时是普通用户，为1时是管理员
    private Integer auth;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date updateTime;
}
