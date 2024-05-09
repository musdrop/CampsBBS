package com.bbsserver.common.dto;

import lombok.Data;

//用于表示前端的分页查询请求
@Data
public class ListDTO {
    private int page = 1;
    private int pageSize = 10;
}
