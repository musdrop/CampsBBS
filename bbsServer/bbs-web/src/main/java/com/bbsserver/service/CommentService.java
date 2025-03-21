package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.entity.bbsComment;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.mapper.CommentMapper;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.utils.SessionManager;
import com.bbsserver.common.vo.CommentVo;
import com.bbsserver.dto.CommentSaveDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private ForumMapper forumMapper;
    
    public void save(CommentSaveDTO commentSaveDTO) {
        // 验证参数
        if (commentSaveDTO.getForumId() == null) {
            throw new CommonException("帖子ID不能为空");
        }
        if (commentSaveDTO.getContent() == null || commentSaveDTO.getContent().trim().isEmpty()) {
            throw new CommonException("评论内容不能为空");
        }
        
        // 检查帖子是否存在
        bbsForum forum = forumMapper.selectById(commentSaveDTO.getForumId());
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        // 获取当前用户ID
        int userId = SessionManager.getUser().getId();
        
        // 创建评论对象
        bbsComment comment = new bbsComment();
        comment.setForumId(commentSaveDTO.getForumId());
        comment.setContent(commentSaveDTO.getContent());
        comment.setUserId(userId);
        comment.setParentId(commentSaveDTO.getParentId()); // 可以为null，表示顶级评论
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setDeleteFlag(0); // 0表示正常
        
        // 保存评论
        commentMapper.insert(comment);
        
        // 更新帖子评论数（如果帖子表有commentCount字段）
        // 此处为简化处理，实际应该通过事务保证原子性
        // forum.setCommentCount(forum.getCommentCount() + 1);
        // forum.setUpdateTime(new Date());
        // forumMapper.updateById(forum);
    }
    
    public Map<String, Object> list(Integer forumId) {
        if (forumId == null) {
            throw new CommonException("帖子ID不能为空");
        }
        
        // 确保帖子存在
        bbsForum forum = forumMapper.selectById(forumId);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        // 查询评论列表（只查询顶级评论，忽略回复）
        QueryWrapper<bbsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forum_id", forumId);
        queryWrapper.eq("delete_flag", 0); // 只查询未删除的评论
        queryWrapper.isNull("parent_id"); // 只查询顶级评论
        queryWrapper.orderByDesc("create_time"); // 按创建时间倒序排序
        
        List<bbsComment> commentList = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = new ArrayList<>();
        
        // 转换为VO对象
        for (bbsComment comment : commentList) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment, commentVo);
            
            // 这里可以添加额外的逻辑，如设置作者信息等
            // commentVo.setAuthorName(getUserName(comment.getUserId()));
            
            // 查询该评论的回复
            commentVo.setReplies(getCommentReplies(comment.getId()));
            
            commentVoList.add(commentVo);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", commentVoList);
        result.put("total", commentList.size());
        
        return result;
    }
    
    // 获取评论的回复列表
    private List<CommentVo> getCommentReplies(Integer parentId) {
        QueryWrapper<bbsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.eq("delete_flag", 0);
        queryWrapper.orderByAsc("create_time"); // 按时间正序排列
        
        List<bbsComment> replyList = commentMapper.selectList(queryWrapper);
        List<CommentVo> replyVoList = new ArrayList<>();
        
        for (bbsComment reply : replyList) {
            CommentVo replyVo = new CommentVo();
            BeanUtils.copyProperties(reply, replyVo);
            
            // 这里可以添加额外的逻辑，如设置作者信息等
            // replyVo.setAuthorName(getUserName(reply.getUserId()));
            
            replyVoList.add(replyVo);
        }
        
        return replyVoList;
    }
    
    // 点赞评论（模拟实现）
    public void likeComment(Integer commentId) {
        bbsComment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleteFlag() == 1) {
            throw new CommonException("评论不存在或已被删除");
        }
        
        // 这里可以实现实际的点赞逻辑
        // 例如增加点赞数、记录谁点赞等
    }
}
