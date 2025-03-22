package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.vo.ForumVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumMapper extends BaseMapper<bbsForum> {

    @Select("SELECT * FROM bbs_forum ${ew.customSqlSegment}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "coverImage", column = "cover_image"),
            @Result(property = "content", column = "content"),
            @Result(property = "viewCount", column = "view_count"),
            @Result(property = "likeCount", column = "like_count"),
            @Result(property = "commentCount", column = "comment_count"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time"),
            @Result(property = "authorId", column = "user_id"),

            @Result(property = "authorName", column = "user_id",javaType = String.class,
                    one = @One(select = "com.bbsserver.common.mapper.UserMapper.selectNameById"))
    })
    Page<bbsForum> forumAndUserList(Page<bbsForum> page, @Param(Constants.WRAPPER)QueryWrapper<bbsForum> queryWrapper);
}
