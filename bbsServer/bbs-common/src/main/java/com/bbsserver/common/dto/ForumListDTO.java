package com.bbsserver.common.dto;

import lombok.Data;

@Data
public class ForumListDTO extends ListDTO {
    private String title;
    private Integer forumId;
    private Integer userId;
}
