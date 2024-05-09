package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbsserver.common.entity.Forum;
import com.bbsserver.common.entity.User;
import com.bbsserver.common.vo.ForumVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumMapper extends BaseMapper<Forum> {

    @Select("select * from forum where delete_flag = 0")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "content", column = "content"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "authorId", column = "user_id"),

            @Result(property = "authorName", column = "user_id",javaType = String.class,
                    one = @One(select = "com.bbsserver.common.mapper.UserMapper.selectNameById"))
    })
    List<ForumVo> forumAndUserList();
}
