package com.bbsserver.web;


import com.bbsserver.common.vo.DataResult;
import com.bbsserver.common.dto.ForumListDTO;
import com.bbsserver.service.ForumAdminService;
import com.bbsserver.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumAdminService forumService;

    @PostMapping("/list")
    public DataResult list(@RequestBody ForumListDTO forumListDTO) {
        //分页查询
        return DataResult.success(forumService.list(forumListDTO));
    }

    @GetMapping("/delete")
    public DataResult delete(@RequestParam int id) {
        forumService.delete(id);
        return DataResult.success();
    }
}
