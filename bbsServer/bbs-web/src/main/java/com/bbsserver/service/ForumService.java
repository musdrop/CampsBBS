package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.dto.ForumListDTO;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.entity.bbsLike;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.mapper.LikeMapper;
import com.bbsserver.common.utils.SessionManager;
import com.bbsserver.common.vo.ForumVo;
import com.bbsserver.dto.ForumSaveDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.ListUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class ForumService {

    @Autowired
    private ForumMapper forumMapper;
    
    @Autowired
    private LikeMapper likeMapper;

    public void save(ForumSaveDTO forumSaveDTO) {
        //从session中获取当前登录用户id
        int userId = SessionManager.getUser().getId();
        log.info("帖子发布:{}", forumSaveDTO);
        bbsForum forum = new bbsForum();
        //将forumSaveDTO的属性拷贝到forum中
        BeanUtils.copyProperties(forumSaveDTO, forum);
        //设置帖子作者id
        forum.setAuthorId(userId);
        //设置时间
        forum.setCreateTime(new Date());
        forum.setUpdateTime(new Date());
        //插入数据库中
        forumMapper.insert(forum);
    }

    public Map<String, Object> list(ForumListDTO listDTO) {
        Map<String, Object> result = new HashMap<>();
        List<ForumVo> forumList = new ArrayList<>();
        
        // 确保分页参数有效
        int pageNum = listDTO.getPageNum() != null ? listDTO.getPageNum() : 1;
        int pageSize = listDTO.getPageSize() != null ? listDTO.getPageSize() : 10;

        int forumId = listDTO.getForumId() != null ? listDTO.getForumId() : -1;
        // 设置查询条件
        QueryWrapper<bbsForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", 0); // 只查询未删除的帖子
        queryWrapper.orderByDesc("create_time"); // 按创建时间倒序排列

        if(forumId!=-1){
            queryWrapper.eq("id",forumId);
        }

        // 按标题模糊查询
        if (listDTO.getTitle() != null && !listDTO.getTitle().trim().isEmpty()) {
            queryWrapper.like("title", listDTO.getTitle().trim());
        }
        
        // 查询特定用户的帖子
        if (listDTO.getUserId() != null) {
            queryWrapper.eq("user_id", listDTO.getUserId());
        }
        
        // 使用MyBatis-Plus的分页功能
        Page<bbsForum> page = new Page<>(pageNum, pageSize);
        Page<bbsForum> forumPage = forumMapper.forumAndUserList(page, queryWrapper);
        
        // 获取分页结果并转换为ForumVo列表
        List<bbsForum> records = forumPage.getRecords();
        
        // 当前登录用户ID，用于判断是否点赞
        Integer currentUserId = null;
        try {
            currentUserId = SessionManager.getUser().getId();
        } catch (Exception e) {
            // 用户未登录，不影响列表获取
        }
        
        for (bbsForum forum : records) {
            ForumVo forumVo = new ForumVo();
            BeanUtils.copyProperties(forum, forumVo);
            
            // 检查当前用户是否点赞过该帖子
            if (currentUserId != null) {
                int liked = likeMapper.checkUserLike(1, forum.getId(), currentUserId);
                forumVo.setIsLiked(liked > 0);
            }
            
            forumList.add(forumVo);
        }
        
        // 返回分页信息和列表数据
        result.put("list", forumList);
        result.put("total", forumPage.getTotal());
        result.put("pages", forumPage.getPages());
        result.put("current", forumPage.getCurrent());
        
        return result;
    }

    public void delete(int id){
        //从session中获取当前登录用户id
        int userId = SessionManager.getUser().getId();
        //查询帖子
        bbsForum forum = forumMapper.selectById(id);
        if(forum == null){
            throw new CommonException("帖子不存在");
        }
        //判断当前用户是否为帖子作者
        if(forum.getAuthorId() != userId){
            throw new CommonException("无权限删除");
        }
        //删除帖子
        forum.setDeleteFlag(1);
        forum.setUpdateTime(new Date());
        forumMapper.updateById(forum);
    }
    
    // 获取帖子详情
    public ForumVo getDetail(Integer id) {
        bbsForum forum = forumMapper.selectById(id);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        ForumVo forumVo = new ForumVo();
        BeanUtils.copyProperties(forum, forumVo);
        
        // 检查当前用户是否点赞过该帖子
        try {
            int userId = SessionManager.getUser().getId();
            int liked = likeMapper.checkUserLike(1, id, userId);
            forumVo.setIsLiked(liked > 0);
        } catch (Exception e) {
            // 用户未登录，不影响详情获取
        }
        
        return forumVo;
    }
    
    // 增加帖子浏览量
    public void incrementViewCount(Integer id) {
        bbsForum forum = forumMapper.selectById(id);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        // 增加浏览量
        forum.setViewCount(forum.getViewCount() == null ? 1 : forum.getViewCount() + 1);
        forum.setUpdateTime(new Date());
        forumMapper.updateById(forum);
    }
    
    // 点赞/取消点赞帖子
    @Transactional
    public Map<String, Object> likeForum(Integer forumId) {
        // 验证帖子是否存在
        bbsForum forum = forumMapper.selectById(forumId);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        // 获取当前用户ID
        int userId = SessionManager.getUser().getId();
        
        // 查询用户是否已点赞
        QueryWrapper<bbsLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 1) // 1表示帖子点赞
                .eq("target_id", forumId)
                .eq("user_id", userId);
        
        bbsLike like = likeMapper.selectOne(queryWrapper);
        boolean isLiked;
        
        if (like == null) {
            // 用户未点赞，添加点赞记录
            like = new bbsLike();
            like.setType(1);
            like.setTargetId(forumId);
            like.setUserId(userId);
            like.setStatus(1);
            like.setCreateTime(new Date());
            like.setUpdateTime(new Date());
            likeMapper.insert(like);
            isLiked = true;
            
            // 更新帖子点赞数
            forum.setLikeCount(forum.getLikeCount() == null ? 1 : forum.getLikeCount() + 1);
        } else if (like.getStatus() == 0) {
            // 之前取消过点赞，现在重新点赞
            like.setStatus(1);
            like.setUpdateTime(new Date());
            likeMapper.updateById(like);
            isLiked = true;
            
            // 更新帖子点赞数
            forum.setLikeCount(forum.getLikeCount() == null ? 1 : forum.getLikeCount() + 1);
        } else {
            // 已点赞，取消点赞
            like.setStatus(0);
            like.setUpdateTime(new Date());
            likeMapper.updateById(like);
            isLiked = false;
            
            // 更新帖子点赞数
            forum.setLikeCount(Math.max((forum.getLikeCount() == null ? 0 : forum.getLikeCount()) - 1, 0));
        }
        
        // 更新帖子信息
        forum.setUpdateTime(new Date());
        forumMapper.updateById(forum);
        
        // 返回点赞状态和最新点赞数
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", forum.getLikeCount());
        return result;
    }
}
