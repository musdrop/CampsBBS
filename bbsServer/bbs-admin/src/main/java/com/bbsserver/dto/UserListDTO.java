package com.bbsserver.dto;

import com.bbsserver.common.dto.ListDTO;
import lombok.Data;


//用于表示前端对用户列表的分页查询请求
@Data
public class UserListDTO extends ListDTO {
    private String account;
}
