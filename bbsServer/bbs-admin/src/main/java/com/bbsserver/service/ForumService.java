package com.bbsserver.service;


import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.vo.ForumVo;
import com.bbsserver.common.vo.PageVo;
import com.bbsserver.dto.ForumListDTO;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ForumService {

    @Autowired
    private ForumMapper forumMapper;

    public PageVo<bbsForum> list(ForumListDTO forumListDTO) {
        //创建分页查询对象
        Page<bbsForum> queryPage = new Page<>(forumListDTO.getPage(), forumListDTO.getPageSize());
        //创建查询条件
        QueryWrapper<bbsForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(bbsForum::getDeleteFlag, 0);
        if (StringUtils.isNotBlank(forumListDTO.getTitle())) {
            queryWrapper.lambda().like(bbsForum::getTitle, forumListDTO.getTitle());
        }
        queryWrapper.lambda().orderByDesc(bbsForum::getCreateTime);
        //分页查询，获得分页查询结果
        IPage<bbsForum> page = forumMapper.selectPage(queryPage, queryWrapper);
        return new PageVo<bbsForum>(page);
    }

    public void delete(int id) {
        bbsForum forum = forumMapper.selectById(id);
        if(forum == null){
            throw new RuntimeException("帖子不存在");
        }
        forum.setDeleteFlag(1);
        forum.setUpdateTime(new Date());
        int count = forumMapper.updateById(forum);
        if(count != 1){
            throw new RuntimeException("删除失败");
        }
    }
}
