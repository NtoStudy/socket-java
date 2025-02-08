package com.socket.socketjava.controller;


import com.socket.socketjava.domain.vo.Notifications.AcceptFriendVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.INotificationsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "系统通知相关接口")
public class NotificationsController {

    @Autowired
    private INotificationsService iNotificationsService;

    @GetMapping("/friend")
    @Operation(summary = "未处理的好友请求")
    public Result<List<AcceptFriendVo>> noAcceptFriend() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<AcceptFriendVo> acceptFriendVoList = iNotificationsService.selectFriend(userId);
        return Result.ok(acceptFriendVoList);
    }

}
