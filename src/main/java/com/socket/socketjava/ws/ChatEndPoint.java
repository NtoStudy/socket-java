package com.socket.socketjava.ws;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.service.IMessagesService;
import com.socket.socketjava.utils.holder.UserHolder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/chat")

public class ChatEndPoint {

    @Autowired
    private IMessagesService iMessagesService;


    private static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        // 用number来作为key存储session对象
        onlineUsers.put(UserHolder.getLoginHolder().getNumber(), session);
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        // 1. 解析接收到的消息为 Messages 对象
        Messages messages = null;
        try {
            messages = new ObjectMapper().readValue(message, Messages.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        messages.setSenderId(UserHolder.getLoginHolder().getUserId()); // 获取当前登录用户的 ID
        // 2. 验证消息是否有效
        if (messages.getSenderId() == null || messages.getReceiverId() == null || messages.getContent() == null) {
            return;
        }
        // 3. 保存消息到数据库
        messages.setSentTime(LocalDateTime.now());
        messages.setIsRead(0);
        messages.setDeletedBySender(0);
        messages.setDeletedByReceiver(0);
        iMessagesService.save(messages);
        // 4. 将消息推送给接收者
        Session receiverSession = onlineUsers.get(messages.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            // 将消息转换为 JSON 并发送
            String response = new ObjectMapper().writeValueAsString(messages);
            receiverSession.getBasicRemote().sendText(response);
        } else {
            System.out.println("请检查服务器状态");
        }
    }

    @OnClose
    public void onClose(Session session) {
        // 从onlineUsers中移除session
        onlineUsers.remove(UserHolder.getLoginHolder().getNumber(), session);
    }
}
