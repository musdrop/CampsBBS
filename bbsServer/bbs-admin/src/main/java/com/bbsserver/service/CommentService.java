package com.bbsserver.service;


import com.bbsserver.common.mapper.CommentMapper;
import com.bbsserver.common.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public List<CommentVo> list(int forumid) {
        return commentMapper.commentAndUserList(forumid);
    }

    public void deleteComment(int id) {
        int count = commentMapper.deleteById(id);
        if(count != 1){
            throw new RuntimeException("删除失败");
        }
    }
}
