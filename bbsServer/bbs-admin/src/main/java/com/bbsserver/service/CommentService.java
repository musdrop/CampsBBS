package com.bbsserver.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbsserver.common.entity.bbsComment;
import com.bbsserver.common.mapper.CommentMapper;
import com.bbsserver.common.mapper.UserMapper;
import com.bbsserver.common.vo.CommentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper bbsUserMapper;

    public List<CommentVo> list(int forumid) {
        List<bbsComment> bbsComments = commentMapper.selectList(new QueryWrapper<bbsComment>().eq("forum_id", forumid));

        // 将bbsComment转换为CommentVo
        List<CommentVo> commentVos = new ArrayList<>();
        for (bbsComment comment : bbsComments) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment, commentVo);
            // 设置用户账号名
            commentVo.setAuthorAccount(bbsUserMapper.selectAccountById(comment.getUserId()));
            // 设置用户名
            commentVo.setAuthorName(bbsUserMapper.selectNameById(comment.getUserId()));
            // 设置用户头像
            commentVo.setAuthorAvatar(bbsUserMapper.selectHeadById(comment.getUserId()));
            commentVos.add(commentVo);
        }
        return commentVos;
    }

    public void deleteComment(int id) {
        int count = commentMapper.deleteById(id);
        if(count != 1){
            throw new RuntimeException("删除失败");
        }
    }
}
