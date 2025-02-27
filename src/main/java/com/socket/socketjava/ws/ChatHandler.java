package com.socket.socketjava.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.socket.socketjava.domain.pojo.GroupMessages;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.service.IGroupMessagesService;
import com.socket.socketjava.service.IMessagesService;
import com.socket.socketjava.service.IUserChatRoomsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatHandler 类继承自 TextWebSocketHandler，用于处理 WebSocket 文本消息
 */
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    // 注入消息服务接口
    @Autowired
    private IMessagesService iMessagesService;
    @Autowired
    private IGroupMessagesService iGroupMessagesService;
    @Autowired
    private IUserChatRoomsService iUserChatRoomsService;

    // 存储在线用户的 WebSocket 会话，使用 ConcurrentHashMap 支持高并发
    private static final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER))
            )
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置目标时区


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
     * @throws IOException 当消息处理中发生 I/O 错误
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            // 1. 解析消息
            Messages messages = objectMapper.readValue(message.getPayload(), Messages.class);
            log.info(String.valueOf(messages));
            messages.setSentTime(LocalDateTime.now());
            messages.setSenderId((Integer) session.getAttributes().get("userId"));

            // 2. 验证消息有效性
            if (messages.getSenderId() == null ||  messages.getContent() == null) {
                log.error("Invalid message: missing required fields");
                return;
            }

            if (Objects.equals(messages.getType(), "friend")) {
                Messages messagesToSave = new Messages();
                messagesToSave.setSentTime(messages.getSentTime());
                messagesToSave.setContent(messages.getContent());
                messagesToSave.setReceiverId(messages.getReceiverId());
                messagesToSave.setSenderId(messages.getSenderId());
                messagesToSave.setIsRead(1);
                // 3. 保存到数据库
                iMessagesService.save(messagesToSave);

                // 4. 转发消息给接收方
                WebSocketSession receiverSession = onlineUsers.get(messagesToSave.getReceiverId());
                if (receiverSession != null && receiverSession.isOpen()) {
                    String response = objectMapper.writeValueAsString(messagesToSave);
                    receiverSession.sendMessage(new TextMessage(response));
                }
            } else if (Objects.equals(messages.getType(), "group")) {
                GroupMessages groupMessages = new GroupMessages();
                groupMessages.setSenderId(messages.getSenderId());
                groupMessages.setChatRoomId(messages.getChatRoomId());
                groupMessages.setContent(messages.getContent());
                groupMessages.setSentTime(messages.getSentTime());
                groupMessages.setIsRead(1);
                // 保存到数据库
                iGroupMessagesService.save(groupMessages);

                // 转到消息给所有人
                List<Integer> groupNumber = iUserChatRoomsService.getRoomUsers(groupMessages.getChatRoomId());

                for (Integer memberId : groupNumber) {
                    if (!Objects.equals(memberId, messages.getSenderId())) {
                        WebSocketSession memberSession = onlineUsers.get(memberId);
                        if (memberSession != null && memberSession.isOpen()) {
                            String response = objectMapper.writeValueAsString(groupMessages);
                            memberSession.sendMessage(new TextMessage(response));
                        }
                    }
                }

            }

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
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
