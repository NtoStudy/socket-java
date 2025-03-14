package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.GroupMessages;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.service.IGroupMessagesService;
import com.socket.socketjava.service.IMessagesService;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.socket.socketjava.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MessageHandlerService {
    @Autowired
    private IMessagesService messagesService;
    @Autowired
    private IGroupMessagesService groupMessagesService;
    @Autowired
    private IUserChatRoomsService userChatRoomsService;
    @Autowired
    private RedisUtils redisUtils;

    public Messages handleFriendMessage(Messages message) {
        Messages messagesToSave = new Messages();
        messagesToSave.setSentTime(LocalDateTime.now())
                .setContent(message.getContent())
                .setReceiverId(message.getReceiverId())
                .setSenderId(message.getSenderId())
                .setMessageType(message.getMessageType())
                .setIsRead(1);

        messagesService.save(messagesToSave);
        clearChatHistoryCache(messagesToSave.getSenderId(), messagesToSave.getReceiverId());

        return messagesToSave;
    }

    public GroupMessages handleGroupMessage(Messages message) {
        GroupMessages groupMessages = new GroupMessages();
        groupMessages.setSenderId(message.getSenderId())
                .setChatRoomId(message.getChatRoomId())
                .setContent(message.getContent())
                .setSentTime(LocalDateTime.now())
                .setMessageType(message.getMessageType())
                .setIsRead(1);

        groupMessagesService.save(groupMessages);
        return groupMessages;
    }

    public List<Integer> getGroupMembers(Integer chatRoomId) {
        return userChatRoomsService.getRoomUsers(chatRoomId);
    }

    private void clearChatHistoryCache(Integer userId, Integer receiverId) {
        String senderKeyPattern = String.format("chat:history:%d:%d:*", userId, receiverId);
        String receiverKeyPattern = String.format("chat:history:%d:%d:*", receiverId, userId);
        redisUtils.deleteByPattern(senderKeyPattern);
        redisUtils.deleteByPattern(receiverKeyPattern);
    }
}