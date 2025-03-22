package com.bbsserver.common.dto;

import lombok.Data;

//用于表示前端的分页查询请求
@Data
public class ListDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
