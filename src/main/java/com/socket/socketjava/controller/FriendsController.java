package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.socket.socketjava.domain.pojo.Friends;
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
@Tag(name = "好友相关接口")
public class FriendsController {

    @Autowired
    private IFriendsService ifriendsService;


    @PostMapping("/add")
    @Operation(summary = "添加好友")
    public Result addFriend(@RequestParam String friendNumber) {
        String userNumber = UserHolder.getLoginHolder().getNumber();
        // 根据用户的number查询到用户的id，将用户的Id写入notifications表中
        ifriendsService.addFriend(userNumber, friendNumber);
        return Result.ok("消息已发出");
    }

    @PutMapping("/accept")
    @Operation(summary = "处理好友请求")
    public Result acceptOrRejectFriend(Integer relationId, Integer status) {
        // 根据用户的number查询到用户的id，将用户的Id写入notifications表中
        ifriendsService.acceptOrRejectFriend(relationId, status);
        if (status == 1) return Result.ok("已接受");
        else if (status == 2) return Result.ok("已拒绝");
        return Result.ok("服务异常");
    }

    @GetMapping("/friendlist")
    @Operation(summary = "查询好友列表")
    public Result<List<FriendVo>> friendList() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<FriendVo> friendList = ifriendsService.friendList(userId);
        return Result.ok(friendList);
    }

    @GetMapping("/messageCount")
    @Operation(summary = "查询好友消息数量")
    public Result messageCount(Integer relationId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        Integer count = ifriendsService.getMessageCount(userId, relationId);
        return Result.ok(count);
    }

    @Operation(summary = "给好友设置备注")
    @PostMapping("/remark")
    public Result remark(@RequestParam Integer friendId, @RequestParam String remark) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        ifriendsService.update()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .set("remark", remark)
                .update();

        Friends friends = new Friends();
        friends.setRemark(remark);
        return Result.ok("修改成功");
    }


    @PutMapping("/togglePin")
    @Operation(summary = "切换好友置顶状态")
    public Result togglePinFriend(@RequestParam Integer friendId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        boolean isPinned = ifriendsService.togglePinFriend(userId, friendId,status);
        return Result.ok(isPinned ? "已置顶" : "已取消置顶");
    }


}
