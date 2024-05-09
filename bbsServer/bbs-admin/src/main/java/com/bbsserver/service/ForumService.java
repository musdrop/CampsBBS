package com.bbsserver.service;


import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.entity.Forum;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.vo.ForumVo;
import com.bbsserver.common.vo.PageVo;
import com.bbsserver.dto.ForumListDTO;
import io.micrometer.common.util.StringUtils;
import org.apache.catalina.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumService {

    @Autowired
    private ForumMapper forumMapper;

    public PageVo<Forum> list(ForumListDTO forumListDTO) {
        //创建分页查询对象
        Page<Forum> queryPage = new Page<>(forumListDTO.getPage(), forumListDTO.getPageSize());
        //创建查询条件
        QueryWrapper<Forum> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Forum::getDeleteFlag, 0);
        if (StringUtils.isNotBlank(forumListDTO.getTitle())) {
            queryWrapper.lambda().like(Forum::getTitle, forumListDTO.getTitle());
        }
        queryWrapper.lambda().orderByDesc(Forum::getCreateTime);
        //分页查询，获得分页查询结果
        IPage<Forum> page = forumMapper.selectPage(queryPage, queryWrapper);
        return new PageVo<Forum>(page);
    }

    public List<List<ForumVo>> list(){
        return ListUtil.partition(forumMapper.forumAndUserList(),4);
    }

    public void delete(int id) {
        Forum forum = forumMapper.selectById(id);
        if(forum == null){
            throw new RuntimeException("帖子不存在");
        }
        forum.setDeleteFlag(1);
        forum.setUpdateTime(LocalDateTime.now());
        int count = forumMapper.updateById(forum);
        if(count != 1){
            throw new RuntimeException("删除失败");
        }
    }
}
