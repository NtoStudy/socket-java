package com.socket.socketjava.controller;


import com.socket.socketjava.domain.vo.Notifications.AcceptFriendVo;
import com.socket.socketjava.domain.vo.Notifications.AcceptRoomsVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.INotificationsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统通知表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@RestController
@RequestMapping("/notifications")
@Tag(name = "系统通知管理")
public class NotificationsController {

    @Autowired
    private INotificationsService iNotificationsService;

    @GetMapping("/friend")
    @Operation(summary = "获取所有未处理的好友请求")
    public Result<List<AcceptFriendVo>> noAcceptFriend() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<AcceptFriendVo> acceptFriendVoList = iNotificationsService.selectFriend(userId);
        return Result.ok(acceptFriendVoList);
    }

    @PostMapping("/friend")
    @Operation(summary = "获取未处理好友请求的数量")
    public Result pushFriend() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        long count = iNotificationsService.countUnhandledFriendRequests(userId);
        return Result.ok(count);
    }

    //TODO后续分析notification的type字段的类型，可以拓展枚举或者是其他的
    @GetMapping("/chatroom")
    @Operation(summary = "获取用户的所有群聊通知")
    public Result<List<AcceptRoomsVo>> noAcceptRooms() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<AcceptRoomsVo> acceptRoomsVoList = iNotificationsService.selectRooms(userId);
        return Result.ok(acceptRoomsVoList);
    }

    @PostMapping("/chatroom")
    @Operation(summary = "获取未处理群聊邀请的数量")
    public Result pushRooms() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        long count = iNotificationsService.countUnhandledRoomInvitations(userId);
        return Result.ok(count);
    }

}
