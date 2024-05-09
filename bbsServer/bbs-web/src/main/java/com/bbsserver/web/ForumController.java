package com.bbsserver.web;

import com.bbsserver.common.dto.ForumListDTO;
import com.bbsserver.common.entity.Forum;
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
    public DataResult delete(@RequestParam("id")Integer id) {
        forumService.delete(id);
        return DataResult.success();
    }
}
