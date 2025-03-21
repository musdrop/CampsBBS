package com.bbsserver.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

    public List<List<ForumVo>> list(ForumListDTO listDTO) {
        //分页查询
        if(listDTO.getPageNum()!=null && listDTO.getPageSize()!=null){
            List<ForumVo> forumVoList = forumMapper.forumAndUserList();
            return ListUtil.partition(forumVoList, 4);
        }


        //按标题查询
        if(listDTO.getTitle()!=null){
            QueryWrapper<bbsForum> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("title", listDTO.getTitle());
            List<bbsForum> forumList = forumMapper.selectList(queryWrapper);
            return ListUtil.partition(forumList.stream().map(forum -> {
                ForumVo forumVo = new ForumVo();
                BeanUtils.copyProperties(forum, forumVo);
                return forumVo;
            }).toList(), 4);
        }
        //查询自己的帖子
        else if(listDTO.getUserId()!=null){
            //从session中获取当前登录用户id
            int userId = SessionManager.getUser().getId();
            //查询当前用户的所有帖子
            QueryWrapper<bbsForum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("delete_flag", 0);
            List<bbsForum> forumList =forumMapper.selectList(queryWrapper);
            return ListUtil.partition(forumList.stream().map(forum -> {
                ForumVo forumVo = new ForumVo();
                BeanUtils.copyProperties(forum, forumVo);
                return forumVo;
            }).toList(), 4);
        }
        //首页展示所有帖子
        else{
            List<ForumVo> forumVoList = forumMapper.forumAndUserList();
            //将帖子按照时间分组
            return ListUtil.partition(forumVoList, 4);
        }
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
        forumMapper.updateById(forum);
    }

}
