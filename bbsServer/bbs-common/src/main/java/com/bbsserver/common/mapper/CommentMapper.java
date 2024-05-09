package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbsserver.common.entity.Comment;
import com.bbsserver.common.vo.CommentVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select * from comment where forum_id = #{forumId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "authorId", column = "user_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "authorName", column = "user_id",javaType = String.class,
                    one = @One(select = "com.bbsserver.common.mapper.UserMapper.selectNameById")),
            @Result(property = "authorAccount", column = "user_id",javaType = String.class,
                    one = @One(select = "com.bbsserver.common.mapper.UserMapper.selectAccountById"))

    })
    List<CommentVo> commentAndUserList(int forumId);
}
