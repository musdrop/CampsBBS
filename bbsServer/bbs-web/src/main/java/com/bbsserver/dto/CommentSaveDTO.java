package com.bbsserver.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentSaveDTO {

    @NotNull
    private Integer forumId;

    @NotBlank
    private String content;
}
