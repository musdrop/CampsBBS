package com.bbsserver.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class bbsComment {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer forumId;

    private Integer userId;

    @TableField(exist = false)
    private String userName;

    private String content;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private Date createTime;
}
