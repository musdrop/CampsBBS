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
public class bbsForum {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    @TableField(value = "cover_image")
    private String coverImage;

    @TableField(value = "user_id")
    private Integer authorId;

    @TableField(exist = false)
    private String authorName;

    private String content;

    // 浏览量
    private Integer viewCount = 0;
    
    // 点赞数
    private Integer likeCount = 0;
    
    // 评论数
    private Integer commentCount = 0;
    
    //为0时正常，为1时被删除
    private int deleteFlag=0;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private Date createTime;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private Date updateTime;
    
    // 前端展示时判断当前用户是否点赞，不保存到数据库
    @TableField(exist = false)
    private Boolean isLiked = false;
}
