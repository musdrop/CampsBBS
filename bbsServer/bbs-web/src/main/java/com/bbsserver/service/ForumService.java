package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbsserver.common.dto.ForumListDTO;
import com.bbsserver.common.entity.bbsForum;
import com.bbsserver.common.exception.CommonException;
import com.bbsserver.common.mapper.ForumMapper;
import com.bbsserver.common.utils.SessionManager;
import com.bbsserver.common.vo.ForumVo;
import com.bbsserver.dto.ForumSaveDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        
        // 设置查询条件
        QueryWrapper<bbsForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", 0); // 只查询未删除的帖子
        queryWrapper.orderByDesc("create_time"); // 按创建时间倒序排列

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
        Page<bbsForum> forumPage = forumMapper.selectPage(page, queryWrapper);
        
        // 获取分页结果并转换为ForumVo列表
        List<bbsForum> records = forumPage.getRecords();
        for (bbsForum forum : records) {
            ForumVo forumVo = new ForumVo();
            BeanUtils.copyProperties(forum, forumVo);
            // 这里可以添加额外的转换逻辑，如设置作者名称等
            // 例如：forumVo.setAuthorName(getUserName(forum.getAuthorId()));
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
    
    // 新增：获取帖子详情
    public ForumVo getDetail(Integer id) {
        bbsForum forum = forumMapper.selectById(id);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        ForumVo forumVo = new ForumVo();
        BeanUtils.copyProperties(forum, forumVo);
        // 这里可以添加额外的逻辑，如设置作者信息等
        
        return forumVo;
    }
    
    // 新增：增加帖子浏览量
    public void incrementViewCount(Integer id) {
        bbsForum forum = forumMapper.selectById(id);
        if (forum == null || forum.getDeleteFlag() == 1) {
            throw new CommonException("帖子不存在或已被删除");
        }
        
        // 这里假设有一个viewCount字段，实际中需要根据你的数据库模型调整
        // forum.setViewCount(forum.getViewCount() + 1);
        forum.setUpdateTime(new Date());
        forumMapper.updateById(forum);
    }
}
