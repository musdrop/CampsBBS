package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.entity.bbsComment;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.entity.bbsLike;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.mapper.CommentMapper;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.mapper.LikeMapper;
import com.bbsserver.common.mapper.UserMapper;
import com.bbsserver.common.utils.SessionManager;
import com.bbsserver.common.vo.CommentVo;
import com.bbsserver.common.vo.PageVo;
import com.bbsserver.dto.CommentSaveDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
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
        
        // 更新帖子评论数
        forum.setCommentCount(forum.getCommentCount() == null ? 1 : forum.getCommentCount() + 1);
        forum.setUpdateTime(new Date());
        forumMapper.updateById(forum);
    }
    
    public PageVo<CommentVo> list(Integer forumId, Integer pageNum, Integer pageSize) {
        if (forumId == null) {
            throw new CommonException("帖子ID不能为空");
        }
        
        // 确保帖子存在
        bbsForum forum = forumMapper.selectById(forumId);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        // 处理分页参数
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        
        // 查询评论列表（只查询顶级评论，忽略回复）
        QueryWrapper<bbsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forum_id", forumId);
        queryWrapper.eq("delete_flag", 0); // 只查询未删除的评论
        queryWrapper.eq("parent_id",0); // 只查询顶级评论
        queryWrapper.orderByDesc("create_time"); // 按创建时间倒序排序
        
        // 使用MyBatis-Plus的分页功能
        Page<bbsComment> page = new Page<>(pageNum, pageSize);
        Page<bbsComment> commentPage = commentMapper.selectPage(page, queryWrapper);
        
        List<bbsComment> commentList = commentPage.getRecords();
        List<CommentVo> commentVoList = new ArrayList<>();
        
        // 当前登录用户ID，用于判断是否点赞
        Integer currentUserId = null;
        try {
            currentUserId = SessionManager.getUser().getId();
        } catch (Exception e) {
            // 用户未登录，不影响列表获取
        }
        
        // 转换为VO对象
        for (bbsComment comment : commentList) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment, commentVo);
            
            // 这里可以添加额外的逻辑，如设置作者信息等
            commentVo.setAuthorName(userMapper.selectNameById(comment.getUserId()));
            commentVo.setAuthorAvatar(userMapper.selectHeadById(comment.getUserId()));
            // 检查当前用户是否点赞过该评论
            if (currentUserId != null) {
                int liked = likeMapper.checkUserLike(2, comment.getId(), currentUserId);
                commentVo.setIsLiked(liked > 0);
            }
            
            // 查询该评论的回复
            commentVo.setReplies(getCommentReplies(comment.getId(), currentUserId));
            
            commentVoList.add(commentVo);
        }
        return new PageVo<CommentVo>(commentPage.getCurrent(), commentPage.getPages(), commentPage.getTotal(), commentVoList);
    }
    
    // 获取评论的回复列表
    private List<CommentVo> getCommentReplies(Integer parentId, Integer currentUserId) {
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
            replyVo.setAuthorName(userMapper.selectNameById(reply.getUserId()));
            // 递归查询回复的回复
            replyVo.setReplies(getCommentReplies(reply.getId(), currentUserId));
            
            // 检查当前用户是否点赞过该回复
            if (currentUserId != null) {
                int liked = likeMapper.checkUserLike(2, reply.getId(), currentUserId);
                replyVo.setIsLiked(liked > 0);
            }
            
            replyVoList.add(replyVo);
        }
        
        return replyVoList;
    }
    
    // 点赞/取消点赞评论
    @Transactional
    public Map<String, Object> likeComment(Integer commentId) {
        // 验证评论是否存在
        bbsComment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleteFlag() == 1) {
            throw new CommonException("评论不存在或已被删除");
        }
        
        // 获取当前用户ID
        int userId = SessionManager.getUser().getId();
        
        // 查询用户是否已点赞
        QueryWrapper<bbsLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 2) // 2表示评论点赞
                .eq("target_id", commentId)
                .eq("user_id", userId);
        
        bbsLike like = likeMapper.selectOne(queryWrapper);
        boolean isLiked;
        
        if (like == null) {
            // 用户未点赞，添加点赞记录
            like = new bbsLike();
            like.setType(2);
            like.setTargetId(commentId);
            like.setUserId(userId);
            like.setStatus(1);
            like.setCreateTime(new Date());
            like.setUpdateTime(new Date());
            likeMapper.insert(like);
            isLiked = true;
            
            // 更新评论点赞数
            comment.setLikeCount(comment.getLikeCount() == null ? 1 : comment.getLikeCount() + 1);
        } else if (like.getStatus() == 0) {
            // 之前取消过点赞，现在重新点赞
            like.setStatus(1);
            like.setUpdateTime(new Date());
            likeMapper.updateById(like);
            isLiked = true;
            
            // 更新评论点赞数
            comment.setLikeCount(comment.getLikeCount() == null ? 1 : comment.getLikeCount() + 1);
        } else {
            // 已点赞，取消点赞
            like.setStatus(0);
            like.setUpdateTime(new Date());
            likeMapper.updateById(like);
            isLiked = false;
            
            // 更新评论点赞数
            comment.setLikeCount(Math.max((comment.getLikeCount() == null ? 0 : comment.getLikeCount()) - 1, 0));
        }
        
        // 更新评论信息
        comment.setUpdateTime(new Date());
        commentMapper.updateById(comment);
        
        // 返回点赞状态和最新点赞数
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", comment.getLikeCount());
        return result;
    }
}
