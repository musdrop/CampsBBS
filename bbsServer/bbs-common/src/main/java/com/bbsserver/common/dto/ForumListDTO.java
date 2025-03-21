package com.bbsserver.common.dto;

import lombok.Data;

@Data
public class ForumListDTO {
    private String title;
    private Integer userId;
    private Integer pageNum;
    private Integer pageSize;
}
