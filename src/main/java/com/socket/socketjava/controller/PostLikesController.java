package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.LikeDetail;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IPostLikesService;
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
 * 动态点赞表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/post-likes")
@Tag(name = "动态点赞管理")
public class PostLikesController {

    @Autowired
    private IPostLikesService postLikesService;
    @Autowired
    private IUserPostsService userPostsService;

    @Operation(summary = "给朋友动态点赞/取消点赞")
    @PostMapping("/likePost")
    public Result<String> likePost(Integer postId, Integer isCancel) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        // 在like表中增添字段
        postLikesService.likePost(postId, userId, isCancel);
        // post表字段加一
        userPostsService.addLikeCount(postId, isCancel);
        return Result.ok(isCancel == 0 ? "点赞成功" : "取消点赞成功");
    }

    @GetMapping("/getByIdToLike")
    @Operation(summary = "查询朋友圈点赞")
    public Result<List<LikeDetail>> getByIdToLike(Integer postId) {
        List<LikeDetail> commentDetailList = postLikesService.getLikeDetail(postId);
        return Result.ok(commentDetailList);
    }


}
