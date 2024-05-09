package com.bbsserver.web;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbsserver.common.entity.Forum;
import com.bbsserver.common.vo.DataResult;
import com.bbsserver.common.vo.PageVo;
import com.bbsserver.dto.ForumListDTO;
import com.bbsserver.service.ForumService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @PostMapping("/list")
    public DataResult list(@RequestBody ForumListDTO forumListDTO) {
        //简单查询所有
        if(forumListDTO.getPageSize()==-1){
            return DataResult.success(forumService.list());
        }
        //分页查询
        return DataResult.success(forumService.list(forumListDTO));
    }

    @GetMapping("/delete")
    public DataResult delete(@RequestParam int id) {
        forumService.delete(id);
        return DataResult.success();
    }
}
