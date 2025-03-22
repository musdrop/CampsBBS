package com.bbsserver.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.TimeZone;

@Data
public class bbsComment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer forumId;
    
    private Integer userId;
    
    @TableField(exist = false)
    private String authorName;
    
    @TableField(exist = false)
    private String authorAvatar;
    
    private Integer parentId = 0;
    
    private String content;
    
    private Integer likeCount = 0;
    
    private Integer deleteFlag = 0;
    
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date updateTime;
}
