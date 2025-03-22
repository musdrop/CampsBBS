package com.bbsserver.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("bbs_like")
public class bbsLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    // 点赞类型：1-帖子点赞，2-评论点赞
    private Integer type;
    
    // 被点赞的对象ID（帖子ID或评论ID）
    private Integer targetId;
    
    // 点赞用户ID
    private Integer userId;
    
    // 点赞状态：1-已点赞，0-取消点赞
    private Integer status = 1;
    
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date updateTime;
}
