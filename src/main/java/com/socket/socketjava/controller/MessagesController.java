package com.socket.socketjava.controller;


import com.socket.socketjava.domain.dto.MessageListDTO;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IMessagesService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 聊天消息表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@RestController
@RequestMapping("/messages")
@Tag(name = "私聊聊天消息相关接口")
public class MessagesController {

    @Autowired
    private IMessagesService iMessagesService;

    @PostMapping("/send")
    @Operation(summary = "发送私聊消息")
    public Result send(Integer receiverId, String content) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        Messages messages = new Messages();
        messages.setSenderId(userId);
        messages.setReceiverId(receiverId);
        messages.setContent(content);
        iMessagesService.save(messages);
        return Result.ok("消息发送成功");
    }

    @GetMapping("/history")
    @Operation(summary = "获取聊天记录")
    public Result<MessageListDTO<Messages>> history(@RequestParam(value = "receiverId") Integer receiverId,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        MessageListDTO<Messages> pageInfo = iMessagesService.getHistoryList(userId, receiverId, pageNum, pageSize);
        return Result.ok(pageInfo);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天记录")
    public Result delete(Integer messageId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        iMessagesService.removeByMessageId(messageId, userId);
        return Result.ok("删除成功");
    }
}
