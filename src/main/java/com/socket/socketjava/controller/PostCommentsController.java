package com.socket.socketjava.controller;


import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IPostCommentsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 动态评论表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/post-comments")
@Tag(name = "动态评论管理")
public class PostCommentsController {

    @Autowired
    private IPostCommentsService postCommentsService;

    @Operation(summary = "给朋友动态评论")
    @PostMapping("/commentPost")
    public Result<String> commentPost(Integer postId, Integer userId, String content, Integer parentCommentId) {
        postCommentsService.commentPost(postId, userId, content, parentCommentId);
        return Result.ok("评论成功");
    }
}
