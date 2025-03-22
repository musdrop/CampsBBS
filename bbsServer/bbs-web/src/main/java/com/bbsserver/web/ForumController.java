package com.bbsserver.web;

import com.bbsserver.common.dto.ForumListDTO;
import com.bbsserver.common.vo.DataResult;
import com.bbsserver.dto.ForumSaveDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.bbsserver.service.ForumService;

@RestController
@RequestMapping("/forum")
public class ForumController {
    @Autowired
    private ForumService forumService;

    @PostMapping("/save")
    public DataResult save(@Valid @RequestBody ForumSaveDTO forumSaveDTO) {
        forumService.save(forumSaveDTO);
        return DataResult.success();
    }

    @PostMapping("/list")
    public DataResult list(@RequestBody ForumListDTO forumListDTO) {
        return DataResult.success(forumService.list(forumListDTO));
    }

    @GetMapping("/delete")
    public DataResult delete(@RequestParam("id") Integer id) {
        forumService.delete(id);
        return DataResult.success();
    }
    
    // 获取帖子详情
    @GetMapping("/detail")
    public DataResult detail(@RequestParam("id") Integer id) {
        return DataResult.success(forumService.getDetail(id));
    }
    
    // 增加帖子浏览量
    @PostMapping("/view")
    public DataResult incrementViewCount(@RequestParam("forumId") Integer forumId) {
        forumService.incrementViewCount(forumId);
        return DataResult.success();
    }
    
    // 点赞/取消点赞帖子
    @PostMapping("/like")
    public DataResult like(@RequestParam("forumId") Integer forumId) {
        return DataResult.success(forumService.likeForum(forumId));
    }
}
