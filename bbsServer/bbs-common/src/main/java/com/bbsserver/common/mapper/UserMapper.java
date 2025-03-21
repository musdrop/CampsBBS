package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbsserver.common.entity.bbsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<bbsUser> {
    @Select("select name from BBS_USER where id = #{id}")
    String selectNameById(int id);

    @Select("select account from BBS_USER where id = #{id}")
    String selectAccountById(int id);
}
