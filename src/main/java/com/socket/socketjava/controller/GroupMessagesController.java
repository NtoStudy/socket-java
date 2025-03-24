package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.MessageListDTO;
import com.socket.socketjava.domain.pojo.GroupMessages;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IGroupMessagesService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 群聊消息表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-09
 */
@RestController
@RequestMapping("/group-messages")
@Tag(name = "群聊消息管理")
public class GroupMessagesController {

    @Autowired
    private IGroupMessagesService groupMessagesService;


    @GetMapping("/history")
    @Operation(summary = "分页获取群聊历史消息")
    public Result<MessageListDTO<GroupMessages>> history(@RequestParam Integer chatRoomId,
                                                         @RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        MessageListDTO<GroupMessages> pageInfo = groupMessagesService.getHistoryList(userId, chatRoomId, pageNum, pageSize);
        return Result.ok(pageInfo);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除指定的群聊消息")
    public Result delete(@RequestParam Integer chatRoomId,@RequestParam Integer messageId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        groupMessagesService.removeBySenderId(chatRoomId, messageId, userId);
        return Result.ok("删除成功");
    }
}
