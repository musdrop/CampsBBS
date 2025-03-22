package com.bbsserver.web;

import com.bbsserver.common.vo.DataResult;
import com.bbsserver.dto.CommentSaveDTO;
import com.bbsserver.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/save")
    public DataResult save(@Valid @RequestBody CommentSaveDTO commentSaveDTO) {
        commentService.save(commentSaveDTO);
        return DataResult.success();
    }

    @GetMapping("/list")
    public DataResult list(
            @RequestParam("forumId") Integer forumId,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return DataResult.success(commentService.list(forumId, pageNum, pageSize));
    }
    
    @PostMapping("/like")
    public DataResult like(@RequestParam("commentId") Integer commentId) {
        return DataResult.success(commentService.likeComment(commentId));
    }
}
