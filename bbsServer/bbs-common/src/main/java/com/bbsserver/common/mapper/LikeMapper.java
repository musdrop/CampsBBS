package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbsserver.common.entity.bbsLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper extends BaseMapper<bbsLike> {
    
    /**
     * 查询用户是否点赞
     */
    @Select("SELECT COUNT(*) FROM bbs_like WHERE type = #{type} AND target_id = #{targetId} AND user_id = #{userId} AND status = 1")
    int checkUserLike(@Param("type") Integer type, @Param("targetId") Integer targetId, @Param("userId") Integer userId);
    
    /**
     * 查询点赞总数
     */
    @Select("SELECT COUNT(*) FROM bbs_like WHERE type = #{type} AND target_id = #{targetId} AND status = 1")
    int countLikes(@Param("type") Integer type, @Param("targetId") Integer targetId);
}
