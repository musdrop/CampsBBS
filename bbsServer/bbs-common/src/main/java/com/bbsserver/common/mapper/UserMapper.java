package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbsserver.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select name from user where id = #{id}")
    String selectNameById(int id);

    @Select("select account from user where id = #{id}")
    String selectAccountById(int id);
}
