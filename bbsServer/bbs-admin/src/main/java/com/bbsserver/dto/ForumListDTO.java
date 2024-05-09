package com.bbsserver.dto;

import com.bbsserver.common.dto.ListDTO;
import lombok.Data;

//用于表示前端对帖子列表的分页查询请求
@Data
public class ForumListDTO extends ListDTO {
    private String title;
}
