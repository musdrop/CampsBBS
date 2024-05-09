package com.bbsserver.common.vo;

import com.bbsserver.common.consts.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentVo {
    private Integer id;
    private Integer authorId;
    private String authorName;
    private String authorAccount;

    private String content;

    @JsonFormat(pattern = CommonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;
}
