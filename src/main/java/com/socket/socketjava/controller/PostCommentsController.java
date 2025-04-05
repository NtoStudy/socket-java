package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IPostCommentsService;
import com.socket.socketjava.service.IUserPostsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

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
    @Autowired
    private IUserPostsService userPostsService;

    @Operation(summary = "给朋友动态评论")
    @PostMapping("/commentPost")
    public Result<String> commentPost(Integer postId , String content, Integer parentCommentId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        postCommentsService.commentPost(postId, userId, content, parentCommentId);
        return Result.ok("评论成功");
    }


    @GetMapping("/getByIdToComment")
    @Operation(summary = "查询朋友圈评论")
    public Result<List<CommentDetail>> getByIdToComment(Integer postId) {
        List<CommentDetail> commentDetailList = postCommentsService.getCommentDetail(postId);
        return Result.ok(commentDetailList);
    }

    @DeleteMapping("/deleteById")
    @Operation(summary = "删除自己的评论")
    public Result<String> deleteById(Integer postId,Integer commentId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        postCommentsService.deleteById(postId,commentId, userId);
        return Result.ok("删除成功");
    }

}
