package com.bbsserver.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.bbsserver.common.consts.CommonConstant;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVo {
    private Integer id;
    
    private Integer forumId;
    
    private Integer userId;
    
    private String authorName;
    
    private String authorAvatar;
    
    private Integer parentId;
    
    private String content;
    
    private Integer likeCount;
    
    private Boolean isLiked = false;
    
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT,timezone = "GMT+8")
    private Date updateTime;

    private List<CommentVo> replies;

    // 管理端专属
    private String authorAccount;
}
