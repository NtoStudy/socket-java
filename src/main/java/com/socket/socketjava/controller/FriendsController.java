package com.socket.socketjava.controller;

import com.socket.socketjava.domain.vo.Friends.FriendVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 好友关系表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@RestController
@RequestMapping("/friends")
@Tag(name = "好友关系管理")
public class FriendsController {

    @Autowired
    private IFriendsService ifriendsService;

    @PostMapping("/add")
    @Operation(summary = "发送好友添加请求")
    public Result addFriend(@RequestParam String friendNumber) {
        String userNumber = UserHolder.getLoginHolder().getNumber();
        // 根据用户的number查询到用户的id，将用户的Id写入notifications表中
        ifriendsService.addFriend(userNumber, friendNumber);
        return Result.ok("消息已发出");
    }

    @PutMapping("/accept")
    @Operation(summary = "接受或拒绝好友请求")
    public Result acceptOrRejectFriend(Integer relationId, Integer status) {
        String message = ifriendsService.handleFriendRequest(relationId, status);
        return Result.ok(message);
    }

    @GetMapping("/friendlist")
    @Operation(summary = "获取当前用户的好友列表")
    public Result<List<FriendVo>> friendList() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<FriendVo> friendList = ifriendsService.friendList(userId);
        return Result.ok(friendList);
    }

    @GetMapping("/messageCount")
    @Operation(summary = "获取与指定好友的未读消息数")
    public Result messageCount(Integer relationId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        Integer count = ifriendsService.getMessageCount(userId, relationId);
        return Result.ok(count);
    }

    @PostMapping("/remark")
    @Operation(summary = "设置好友的备注名称")
    public Result remark(@RequestParam Integer friendId, @RequestParam String remark) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        ifriendsService.setFriendRemark(userId, friendId, remark);
        return Result.ok("修改成功");
    }

    @PutMapping("/togglePin")
    @Operation(summary = "设置或取消好友置顶状态")
    public Result togglePinFriend(@RequestParam Integer friendId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        boolean isPinned = ifriendsService.togglePinFriend(userId, friendId, status);
        return Result.ok(isPinned ? "已置顶" : "已取消置顶");
    }

}
