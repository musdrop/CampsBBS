package com.bbsserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentSaveDTO {
    @NotNull(message = "帖子ID不能为空")
    private Integer forumId;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    // 父评论ID，为0表示顶级评论
    private Integer parentId;
}
