package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.GroupIsContainerUser;
import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
import com.socket.socketjava.domain.vo.Chatroom.GroupCountVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IChatRoomsService;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "群聊管理")
public class UserChatRoomsController {

    @Autowired
    private IUserChatRoomsService userChatRoomsService;
    @Autowired
    private IChatRoomsService chatRoomsService;
    @Autowired

    @PostMapping("/create")
    @Operation(summary = "创建新的群聊")
    public Result create(@RequestBody CreateRoomVo createRoomVo) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        String groupNumber = userChatRoomsService.createChatRoom(userId, createRoomVo);
        return Result.ok(groupNumber);
    }

    @GetMapping("/inquire")
    @Operation(summary = "通过群号查询群聊信息")
    public Result<GroupIsContainerUser> inquire(String groupNumber) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupIsContainerUser groupIsContainerUser = userChatRoomsService.inquireGroup(groupNumber, userId);
        return Result.ok(groupIsContainerUser);
    }

    @GetMapping("/getById")
    @Operation(summary = "通过ID查询群聊详情")
    public Result<ChatRooms> getById(Integer roomId) {
        ChatRooms chatRoom = chatRoomsService.getById(roomId);
        return Result.ok(chatRoom);
    }

    // TODO发送的通知只有群主或者管理员才能收得到
    @PostMapping("/addgroup")
    @Operation(summary = "申请加入指定群聊")
    public Result addGroup(String groupNumber) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.addGroup(userId, groupNumber);
        return Result.ok("申请成功");
    }

    @PutMapping("/accept")
    @Operation(summary = "接受或拒绝群聊邀请") 
    public Result acceptOrReject(Integer roomId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.acceptOrRejectChatRoom(userId, roomId, status);
        if (status == 1) return Result.ok("已加入群聊");
        else if (status == 2) return Result.ok("已拒绝群聊");
        return Result.ok("服务异常");
    }

    @GetMapping("/roomlist")
    @Operation(summary = "获取用户加入的所有群聊") 
    public Result<List<ChatRoomListVo>> roomList() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<ChatRoomListVo> list = userChatRoomsService.getRoomList(userId);
        return Result.ok(list);
    }

    @GetMapping("/roomUsers")
    @Operation(summary = "获取群聊的所有成员ID")
    public Result roomUsers(Integer roomId) {
        List<Integer> usersIds = userChatRoomsService.getRoomUsers(roomId);
        return Result.ok(usersIds);
    }

    @Operation(summary = "获取群聊中的未读消息数量")
    @GetMapping("/messageCount")
    public Result messageCount(Integer roomId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        Integer count = userChatRoomsService.getMessageCount(userId, roomId);
        return Result.ok(count);
    }

    //TODO 置顶群聊，创建群聊管理群聊，加入群的数量
    @Operation(summary = "获取用户置顶的群聊列表") 
    @GetMapping("/pinnedGroup")
    public Result pinnedGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getPinnedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "设置或取消群聊置顶状态")
    @PostMapping("/pinnedGroup")
    public Result setPinnedGroup(Integer roomId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.setPinnedGroup(userId, roomId, status);
        return Result.ok(status == 1 ? "置顶成功" : "取消置顶成功");
    }


    @Operation(summary = "获取用户创建的群聊列表")
    @GetMapping("/createGroup")
    public Result createGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getCreatedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "获取用户管理的群聊列表") 
    @GetMapping("/manageGroup")
    public Result manageGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getManagedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "获取用户加入的群聊列表")
    @GetMapping("/joinGroup")
    public Result joinGroup() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        GroupCountVo groupCountVo = userChatRoomsService.getJoinedGroups(userId);
        return Result.ok(groupCountVo);
    }

    @Operation(summary = "修改用户在群聊中的昵称") 
    @PutMapping("/nickname")
    public Result updateNickname(String nickname, Integer roomId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        boolean result = userChatRoomsService.updateNickname(userId, roomId, nickname);
        return Result.ok(result);
    }

    @Operation(summary = "邀请好友加入群聊")
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
