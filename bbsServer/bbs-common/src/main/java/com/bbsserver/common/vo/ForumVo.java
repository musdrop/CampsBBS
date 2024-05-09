package com.bbsserver.common.vo;


import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ForumVo {
    private Integer id;
    private String title;
    private String coverImage;

    private Integer authorId;
    private String authorName;

    private String content;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;
    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;
}
