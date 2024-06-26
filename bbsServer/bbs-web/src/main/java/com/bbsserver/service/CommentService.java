package com.bbsserver.service;


import com.bbsserver.common.entity.Comment;
import com.bbsserver.common.mapper.CommentMapper;
import com.bbsserver.common.utils.SessionManager;
import com.bbsserver.common.vo.CommentVo;
import com.bbsserver.dto.CommentSaveDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public void save(CommentSaveDTO commentSaveDTO) {
        log.info("评论发布");
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentSaveDTO, comment);
        comment.setUserId(SessionManager.getUser().getId());
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
    }

    public List<CommentVo> list(Integer forumId) {
        return commentMapper.commentAndUserList(forumId);
    }
}
