package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.mapper.CommentMapper;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class InfoService {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ForumMapper forumMapper;

    // 获取论坛各种相关信息
    public Map<String,Object> getInfos() {
        // 获取评论数
        Integer commentCount = commentMapper.selectList(null).size();
        // 获取用户数
        Integer userCount = userMapper.selectList(null).size();
        // 获取帖子数
        Integer forumCount = forumMapper.selectList(null).size();
        // 获取今日新增帖子数
        QueryWrapper<bbsForum> queryWrapper = new QueryWrapper<>();
        // 创建今日凌晨时间
        Date today = new Date();
        today.setHours(0);
        queryWrapper.lambda().ge(bbsForum::getCreateTime, today);
        Integer todayForumCount = forumMapper.selectList(queryWrapper).size();
        Map<String,Object> result = new HashMap<>();
        result.put("commentCount",commentCount);
        result.put("userCount",userCount);
        result.put("forumCount",forumCount);
        result.put("todayForumCount",todayForumCount);
        return result;
    }
}
