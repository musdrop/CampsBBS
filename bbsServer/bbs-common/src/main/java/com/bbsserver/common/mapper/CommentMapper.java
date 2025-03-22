package com.bbsserver.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbsserver.common.entity.bbsComment;
import com.bbsserver.common.vo.CommentVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<bbsComment> {
}
