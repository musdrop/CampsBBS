package com.bbsserver.common.vo;


import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class ForumVo {
    private Integer id;
    private String title;
    private String coverImage;

    private Integer authorId;
    private String authorName;
    private Boolean isLiked = false;

    // 浏览量
    private Integer viewCount = 0;

    // 点赞数
    private Integer likeCount = 0;

    // 评论数
    private Integer commentCount = 0;

    private String content;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private Date createTime;
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private Date updateTime;
}
