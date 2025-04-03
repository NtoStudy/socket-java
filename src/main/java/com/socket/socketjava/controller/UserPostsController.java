package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.dto.LikeDetail;
import com.socket.socketjava.domain.dto.PostDetail;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IUserPostsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * <p>
 * 用户动态表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/user-posts")
@Tag(name = "用户动态管理")
public class UserPostsController {

    @Autowired
    private IUserPostsService userPostsService;


    @PostMapping("/create")
    @Operation(summary = "发布朋友圈")
    public Result createPost(String content, String mediaType, String mediaUrl) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userPostsService.createPost(userId,content,mediaType,mediaUrl);
        return Result.ok("发布成功");
    }

    @GetMapping("/getById")
    @Operation(summary = "查询朋友圈详情")
    public Result<PostDetail> getById(Integer postId) {
        PostDetail postDetail =  userPostsService.getPostDetail(postId);
        return Result.ok(postDetail);
    }

    @GetMapping("/getByIdToComment")
    @Operation(summary = "查询朋友圈评论")
    public Result<List<CommentDetail>> getByIdToComment(Integer postId) {
        List<CommentDetail> commentDetailList = userPostsService.getCommentDetail(postId);
        return Result.ok(commentDetailList);
    }

    @GetMapping("/getByIdToLike")
    @Operation(summary = "查询朋友圈点赞")
    public Result<List<LikeDetail>> getByIdToLike(Integer postId) {
        List<LikeDetail> commentDetailList = userPostsService.getLikeDetail(postId);
        return Result.ok(commentDetailList);
    }


}
