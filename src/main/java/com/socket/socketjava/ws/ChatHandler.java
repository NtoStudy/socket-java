package com.socket.socketjava.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.socket.socketjava.domain.pojo.GroupMessages;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.service.impl.MessageHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatHandler 类继承自 TextWebSocketHandler，用于处理 WebSocket 文本消息
 */
@Slf4j
public class ChatHandler extends BinaryWebSocketHandler {

    @Autowired
    private MessageHandlerService messageHandlerService;
    // 存储在线用户的 WebSocket 会话，使用 ConcurrentHashMap 支持高并发
    private static final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER)))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置目标时区
    // 保存文件分片的临时存储，key: fileName，value: Map<chunkNumber, chunkData>
    private static final ConcurrentHashMap<String, Map<Integer, byte[]>> tempFileStorage = new ConcurrentHashMap<>();

    // 保存文件上传进度，key: fileName，value: Map<totalChunks, uploadedChunks>
    private static final ConcurrentHashMap<String, Map<String, Integer>> uploadProgress = new ConcurrentHashMap<>();


    /**
     * 在建立连接后执行的操作
     *
     * @param session 当前用户的 WebSocket 会话
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取用户信息并存储 session
        Integer userId = (Integer) session.getAttributes().get("userId");
        onlineUsers.put(userId, session);
        System.out.println("当前websocket登录id" + userId);
    }

    /**
     * 处理接收到的文本消息
     *
     * @param session 当前用户的 WebSocket 会话
     * @param message 接收到的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("Received text message: {}", payload);
        try {
            // 1. 解析消息
            Messages messages = objectMapper.readValue(message.getPayload(), Messages.class);

            messages.setSentTime(LocalDateTime.now());
            messages.setSenderId((Integer) session.getAttributes().get("userId"));
            log.info("Received message: {}", messages);

            // 2. 验证消息有效性
            if (messages.getSenderId() == null || messages.getContent() == null) {
                log.error("Invalid message: missing required fields");
                return;
            }

            // 3. 根据消息类型处理消息
            if (Objects.equals(messages.getType(), "friend")) {
                handleFriendMessage(messages, session);
            } else if (Objects.equals(messages.getType(), "group")) {
                handleGroupMessage(messages);
            }

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }

    }


    private void handleFriendMessage(Messages message, WebSocketSession session) throws IOException {
        Messages savedMessage = messageHandlerService.handleFriendMessage(message);

        WebSocketSession receiverSession = onlineUsers.get(savedMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            String response = objectMapper.writeValueAsString(savedMessage);
            receiverSession.sendMessage(new TextMessage(response));
        }
    }
    private void handleGroupMessage(Messages message) throws IOException {
        GroupMessages groupMessage = messageHandlerService.handleGroupMessage(message);
        List<Integer> groupMembers = messageHandlerService.getGroupMembers(groupMessage.getChatRoomId());

        for (Integer memberId : groupMembers) {
            if (!Objects.equals(memberId, message.getSenderId())) {
                WebSocketSession memberSession = onlineUsers.get(memberId);
                if (memberSession != null && memberSession.isOpen()) {
                    String response = objectMapper.writeValueAsString(groupMessage);
                    memberSession.sendMessage(new TextMessage(response));
                }
            }
        }
    }

    /**
     * 在连接关闭后执行的操作
     *
     * @param session 当前用户的 WebSocket 会话
     * @param status  连接关闭的状态
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
