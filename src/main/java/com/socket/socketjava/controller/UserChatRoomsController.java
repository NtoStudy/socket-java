package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.socket.socketjava.domain.dto.GroupIsContainerUser;
import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.domain.pojo.Notifications;
import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
import com.socket.socketjava.domain.vo.Chatroom.GroupCountVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IChatRoomsService;
import com.socket.socketjava.service.INotificationsService;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户-聊天室关联表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@RestController
@RequestMapping("/chat-rooms")
@Tag(name = "聊天室相关接口")
public class UserChatRoomsController {

    @Autowired
    private IUserChatRoomsService userChatRoomsService;
    @Autowired
    private IChatRoomsService chatRoomsService;
    @Autowired
    private INotificationsService notificationsService;

    @PostMapping("/create")
    @Operation(summary = "新建群聊")
    public Result create(@RequestBody CreateRoomVo createRoomVo) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        String groupNumber = userChatRoomsService.createChatRoom(userId, createRoomVo);
        return Result.ok(groupNumber);
    }

    @GetMapping("/inquire")
    @Operation(summary = "查询群聊")
    public Result<GroupIsContainerUser> inquire(String groupNumber) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupIsContainerUser groupIsContainerUser = userChatRoomsService.inquireGroup(groupNumber, userId);
        return Result.ok(groupIsContainerUser);
    }

    @GetMapping("/getById")
    @Operation(summary = "通过id查询群聊信息")
    public Result<ChatRooms> getById(Integer roomId) {
        ChatRooms chatRoom = chatRoomsService.getById(roomId);
        return Result.ok(chatRoom);
    }

    // TODO发送的通知只有群主或者管理员才能收得到
    @PostMapping("/addgroup")
    @Operation(summary = "申请加入群聊")
    public Result addGroup(String groupNumber) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.addGroup(userId, groupNumber);
        return Result.ok("申请成功");
    }

    @PutMapping("/accept")
    @Operation(summary = "处理群聊邀请")
    public Result acceptOrReject(Integer roomId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.acceptOrRejectChatRoom(userId, roomId, status);
        if (status == 1) return Result.ok("已加入群聊");
        else if (status == 2) return Result.ok("已拒绝群聊");
        return Result.ok("服务异常");
    }

    @Operation(summary = "获取群聊列表")
    @GetMapping("/roomlist")
    public Result<List<ChatRoomListVo>> roomList() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<ChatRoomListVo> list = userChatRoomsService.getRoomList(userId);
        return Result.ok(list);
    }

    @Operation(summary = "通过群获取群成员信息")
    @GetMapping("/roomUsers")
    public Result roomUsers(Integer roomId) {
        List<Integer> usersId = userChatRoomsService.getRoomUsers(roomId);
        return Result.ok(usersId);
    }

    @Operation(summary = "获取群聊未读消息")
    @GetMapping("/messageCount")
    public Result messageCount(Integer roomId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        Integer count = userChatRoomsService.getMessageCount(userId, roomId);
        return Result.ok(count);
    }

    //TODO 置顶群聊，创建群聊管理群聊，加入群的数量
    @Operation(summary = "获取置顶群聊信息")
    @GetMapping("/pinnedGroup")
    public Result pinnedGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getPinnedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "置顶群聊")
    @PostMapping("/pinnedGroup")
    public Result setPinnedGroup(Integer roomId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        boolean result = userChatRoomsService.setPinnedGroup(userId, roomId, status);
        return Result.ok(status == 1 ? "置顶成功" : "取消置顶成功");
    }


    @Operation(summary = "获取创建群聊信息")
    @GetMapping("/createGroup")
    public Result createGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getCreatedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "获取管理群聊信息")
    @GetMapping("/manageGroup")
    public Result manageGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getManagedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "加入群聊信息")
    @GetMapping("/joinGroup")
    public Result joinGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getJoinedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "修改自己本群昵称")
    @PutMapping("/nickname")
    public Result updateNickname(String nickname, Integer roomId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        boolean result = userChatRoomsService.updateNickname(userId, roomId, nickname);
        return Result.ok(result);
    }

    @Operation(summary = "邀请新成员")
    @PostMapping("/invite")
    public Result invite(@RequestBody List<Integer> FriendIds, Integer roomId) {
        userChatRoomsService.inviteToGroup(FriendIds, roomId);
        return Result.ok("邀请已发出");
    }

    @Operation(summary = "退出或者解散群聊")
    @PutMapping("/quit")
    public Result quit(Integer roomId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        String message = userChatRoomsService.quitOrDismissGroup(userId, roomId);
        return Result.ok(message);
    }
}
