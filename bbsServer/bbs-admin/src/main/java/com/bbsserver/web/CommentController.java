package com.bbsserver.web;


import com.bbsserver.common.vo.DataResult;
import com.bbsserver.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public DataResult list(@RequestParam Integer forumId) {
        return DataResult.success(commentService.list(forumId));
    }

    @GetMapping("/delete")
    public DataResult delete(@RequestParam Integer id) {
        commentService.deleteComment(id);
        return DataResult.success();
    }
}
