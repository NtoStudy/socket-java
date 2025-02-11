package com.socket.socketjava.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.service.IMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatHandler 类继承自 TextWebSocketHandler，用于处理 WebSocket 文本消息
 */
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    // 注入消息服务接口
    @Autowired
    private IMessagesService iMessagesService;

    // 存储在线用户的 WebSocket 会话，使用 ConcurrentHashMap 支持高并发
    private static final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 在建立连接后执行的操作
     * @param session 当前用户的 WebSocket 会话
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取用户信息并存储 session
        Integer userId = (Integer) session.getAttributes().get("userId");
        onlineUsers.put(userId, session);
        System.out.println("WebSocket connection established for user: " + userId);
    }

    /**
     * 处理接收到的文本消息
     * @param session 当前用户的 WebSocket 会话
     * @param message 接收到的文本消息
     * @throws IOException 当消息处理中发生 I/O 错误
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 1. 解析接收到的消息为 Messages 对象
        Messages messages = null;
        try {
            messages = new ObjectMapper().readValue(message.getPayload(), Messages.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        // 2. 设置消息发送者的 ID
        Integer userId = (Integer) session.getAttributes().get("userId");
        messages.setSenderId(userId);

        // 3. 验证消息是否有效
        if (messages.getSenderId() == null || messages.getReceiverId() == null || messages.getContent() == null) {
            return;
        }

        // 4. 保存消息到数据库
        messages.setIsRead(0);
        messages.setDeletedBySender(0);
        messages.setDeletedByReceiver(0);
        iMessagesService.save(messages);

        // 5. 将消息推送给接收者
        WebSocketSession receiverSession = onlineUsers.get(messages.getReceiverId());
        log.info("Receiver session: {}", receiverSession);
        if (receiverSession != null && receiverSession.isOpen()) {
            String response = new ObjectMapper().writeValueAsString(messages);
            receiverSession.sendMessage(new TextMessage(response));
            log.info("Message sent to receiver: " + new TextMessage(response));
        } else {
            System.out.println("Receiver session not found or closed");
        }
    }

    /**
     * 在连接关闭后执行的操作
     * @param session 当前用户的 WebSocket 会话
     * @param status 连接关闭的状态
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 从在线用户中移除 session
        Integer userId = (Integer) session.getAttributes().get("userId");
        onlineUsers.remove(userId);
        System.out.println("WebSocket connection closed for user: " + userId);
    }
}
