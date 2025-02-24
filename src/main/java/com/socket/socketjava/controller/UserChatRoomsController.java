package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
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
@Tag(name="聊天室相关接口")
public class UserChatRoomsController {

    @Autowired
    private IUserChatRoomsService userChatRoomsService;
    @Autowired
    private IChatRoomsService ChatRoomsService;

    @PostMapping("/create")
    @Operation(summary = "新建群聊")
    public Result create(@RequestBody CreateRoomVo createRoomVo){
        Integer userId = UserHolder.getLoginHolder().getUserId();
        String groupNumber =  userChatRoomsService.createChatRoom(userId,createRoomVo);
        return Result.ok(groupNumber);
    }

    @GetMapping("/inquire")
    @Operation(summary = "查询群聊")
    public Result<ChatRooms> inquire(String groupNumber){
        LambdaQueryWrapper<ChatRooms> chatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatRoomsLambdaQueryWrapper.eq(ChatRooms::getGroupNumber,groupNumber);
        ChatRooms chatRoom = ChatRoomsService.getOne(chatRoomsLambdaQueryWrapper);
        return Result.ok(chatRoom);
    }

    @PostMapping("/addgroup")
    @Operation(summary = "申请加入群聊")
    public Result addGroup(String groupNumber){
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.addGroup(userId,groupNumber);
        return Result.ok("申请成功");
    }

    @PutMapping("/accept")
    @Operation(summary = "处理群聊邀请")
    public Result acceptOrReject(Integer roomId, Integer status){
        Integer userId = UserHolder.getLoginHolder().getUserId();
        userChatRoomsService.acceptOrRejectChatRoom(userId,roomId,status);
        if(status == 1) return Result.ok("已加入群聊");
        else if (status == 2)return Result.ok("已拒绝群聊");
        return Result.ok("服务异常");
    }

    @Operation(summary = "获取群聊列表")
    @GetMapping("/roomlist")
    public Result<List<ChatRoomListVo>> roomList(){
        Integer userId = UserHolder.getLoginHolder().getUserId();
        List<ChatRoomListVo> list = userChatRoomsService.getRoomList(userId);
        return Result.ok(list);
    }

    @Operation(summary = "通过群获取群成员信息")
    @GetMapping("/roomUsers")
    public Result roomUsers(Integer roomId){
        List<Integer> usersId = userChatRoomsService.getRoomUsers(roomId);
        return Result.ok(usersId);
    }

    @Operation(summary = "获取群聊未读消息")
    @GetMapping("/messageCount")
    public Result messageCount(Integer roomId){
        Integer userId = UserHolder.getLoginHolder().getUserId();
        Integer count = userChatRoomsService.getMessageCount(userId,roomId);
        return Result.ok(count);
    }


}
