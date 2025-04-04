package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.dto.LikeDetail;
import com.socket.socketjava.domain.dto.PageList;
import com.socket.socketjava.domain.dto.PostDetail;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IUserPostsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        userPostsService.createPost(userId, content, mediaType, mediaUrl);
        return Result.ok("发布成功");
    }

    @Operation(summary = "查询自己发布的朋友圈列表")
    @GetMapping("/getList")
    public Result<PageList<PostDetail>> getList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        PageList<PostDetail> pageInfo = userPostsService.getPostList(userId, pageNum, pageSize);
        return Result.ok(pageInfo);
    }

    @GetMapping("/getById")
    @Operation(summary = "查询朋友圈详情")
    public Result<PostDetail> getById(Integer postId) {
        PostDetail postDetail = userPostsService.getPostDetail(postId);
        return Result.ok(postDetail);
    }

    @Operation(summary = "给朋友圈置顶/取消置顶")
    @PostMapping("/topPost")
    public Result<String> topPost(Integer postId, Integer isTop) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userPostsService.topPost(userId, postId, isTop);
        return Result.ok(isTop == 1 ? "置顶成功" : "取消置顶成功");
    }


//    @Operation(summary = "查询朋友圈列表")
//    @GetMapping("/getList")
//    public Result<PageList<PostDetail>> getList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
//                                                @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
//        Integer userId = UserHolder.getLoginHolder().getUserId();
//        PageList<PostDetail> pageInfo = userPostsService.getPostList(userId, pageNum, pageSize);
//        return Result.ok(pageInfo);
//    }

}
