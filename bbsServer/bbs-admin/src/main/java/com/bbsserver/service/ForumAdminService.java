package com.bbsserver.service;

import com.bbsserver.common.mapper.ForumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumAdminService extends ForumService {
    @Autowired
    private ForumMapper forumMapper;

    public void delete(int id) {
        int count = forumMapper.deleteById(id);
        if(count != 1){
            throw new RuntimeException("删除失败");
        }
    }
}
