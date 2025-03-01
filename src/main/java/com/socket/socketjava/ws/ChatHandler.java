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
import com.socket.socketjava.utils.AliyunOssOperator;
import com.socket.socketjava.utils.holder.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatHandler 类继承自 TextWebSocketHandler，用于处理 WebSocket 文本消息
 */
@Slf4j
public class ChatHandler extends BinaryWebSocketHandler {

    // 注入消息服务接口
    @Autowired
    private IMessagesService iMessagesService;
    @Autowired
    private IGroupMessagesService iGroupMessagesService;
    @Autowired
    private IUserChatRoomsService iUserChatRoomsService;
    @Autowired
    private AliyunOssOperator aliyunOssOperator;

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
                // 处理好友消息
                Messages messagesToSave = new Messages();
                messagesToSave.setSentTime(messages.getSentTime())
                        .setContent(messages.getContent())
                        .setReceiverId(messages.getReceiverId())
                        .setSenderId(messages.getSenderId())
                        .setMessageType(messages.getMessageType())
                        .setIsRead(1);
                // 保存到数据库
                iMessagesService.save(messagesToSave);

                // 4. 转发消息给接收方
                WebSocketSession receiverSession = onlineUsers.get(messagesToSave.getReceiverId());
                if (receiverSession != null && receiverSession.isOpen()) {
                    String response = objectMapper.writeValueAsString(messagesToSave);
                    receiverSession.sendMessage(new TextMessage(response));
                }
            } else if (Objects.equals(messages.getType(), "group")) {
                // 处理群组消息
                GroupMessages groupMessages = new GroupMessages();
                groupMessages.setSenderId(messages.getSenderId())
                        .setChatRoomId(messages.getChatRoomId())
                        .setContent(messages.getContent())
                        .setSentTime(messages.getSentTime())
                        .setMessageType(messages.getMessageType())
                        .setIsRead(1);
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
     * 处理接收到的二进制消息
     *
     * @param session 当前用户的 WebSocket 会话
     * @param message 接收到的二进制消息
     * @throws IOException 可能抛出的异常
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        byte[] payload = new byte[message.getPayload().remaining()];
        message.getPayload().get(payload);

        // 解析元数据和文件分片
        int metaDataLength = 128; // 元数据长度（假设128字节）
        String metaDataString = new String(payload, 0, metaDataLength).trim();
        byte[] chunkData = Arrays.copyOfRange(payload, metaDataLength, payload.length);

        try {
            // 解析元数据
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> metaData = objectMapper.readValue(metaDataString, Map.class);
            String messageType = (String) metaData.get("messageType");
            Integer receiverId = (Integer) metaData.get("receiverId");
            String type = (String) metaData.get("type");
            Integer chatRoomId = (Integer) metaData.get("chatRoomId");

            // 存储文件分片
            int chunkNumber = ByteBuffer.wrap(chunkData, 0, 4).getInt();
            String fileName = new String(chunkData, 4, 128).trim();
            byte[] fileChunk = Arrays.copyOfRange(chunkData, 132, chunkData.length);

            // 更新文件分片存储
            tempFileStorage.computeIfAbsent(fileName, k -> new ConcurrentHashMap<>()).put(chunkNumber, fileChunk);

            // 判断是否是最后一个分片
            if (isLastChunk(session, fileName, chunkNumber)) {
                rebuildFile(fileName, messageType, receiverId, chatRoomId, type);
            }
        } catch (Exception e) {
            log.error("Error processing binary message: {}", e.getMessage(), e);
        }
    }

    /**
     * 判断是否是最后一个分片
     * 假设分片编号从0开始，总分片数由前端通知
     */
    private boolean isLastChunk(WebSocketSession session, String fileName, int chunkNumber) {
        // 前端应在发送文件前发送总分片数
        // 这里假设总分片数存储在 session 的 attribute 中
        Map<String, Object> attributes = session.getAttributes();
        Integer totalChunks = (Integer) attributes.get("totalChunks");
        if (totalChunks == null) {
            log.error("Total chunks not found for file: {}", fileName);
            return false;
        }

        return chunkNumber == totalChunks - 1;
    }

    /**
     * 重组文件
     */
    private void rebuildFile(String fileName, String messageType, Integer receiverId, Integer chatRoomId, String type) throws IOException {
        Map<Integer, byte[]> chunks = tempFileStorage.get(fileName);
        if (chunks == null || chunks.isEmpty()) {
            log.error("No chunks found for file: {}", fileName);
            return;
        }
        // 按分片编号排序
        Map<Integer, byte[]> sortedChunks = new TreeMap<>(chunks);
        byte[] fileData = new byte[0];
        for (byte[] chunk : sortedChunks.values()) {
            byte[] newFileData = new byte[fileData.length + chunk.length];
            System.arraycopy(fileData, 0, newFileData, 0, fileData.length);
            System.arraycopy(chunk, 0, newFileData, fileData.length, chunk.length);
            fileData = newFileData;
        }
        // 构建唯一的文件名
        String objectName = "uploads/" + UUID.randomUUID() + "-" + fileName;
        // 上传到阿里云 OSS
        String fileUrl = aliyunOssOperator.uploadBytes(fileData, objectName);
        // 写入到数据库中
        if (Objects.equals(type, "friend")) {
            Messages messages = new Messages();
            messages.setMessageType(messageType)
                    .setContent(fileUrl)
                    .setReceiverId(receiverId)
                    .setSenderId(UserHolder.getLoginHolder().getUserId())
                    .setSentTime(LocalDateTime.now())
                    .setIsRead(1);

            iMessagesService.save(messages);

            WebSocketSession receiverSession = onlineUsers.get(receiverId);
            if (receiverSession != null && receiverSession.isOpen()) {
                String response = objectMapper.writeValueAsString(messages);
                receiverSession.sendMessage(new TextMessage(response));
            }

        } else if (Objects.equals(type, "group")) {
            GroupMessages groupMessages = new GroupMessages();
            groupMessages.setMessageType(messageType)
                    .setContent(fileUrl)
                    .setChatRoomId(chatRoomId)
                    .setSenderId(UserHolder.getLoginHolder().getUserId())
                    .setSentTime(LocalDateTime.now())
                    .setIsRead(1);
            iGroupMessagesService.save(groupMessages);
            List<Integer> groupMembers = iUserChatRoomsService.getRoomUsers(chatRoomId);
            for (Integer memberId : groupMembers) {
                if (Objects.equals(memberId, UserHolder.getLoginHolder().getUserId())) {
                    continue; // 不需要发送给消息发送者
                }

                WebSocketSession memberSession = onlineUsers.get(memberId);
                if (memberSession != null && memberSession.isOpen()) {
                    String response = objectMapper.writeValueAsString(groupMessages);
                    memberSession.sendMessage(new TextMessage(response));
                }
            }
        }

        if (fileUrl != null) {
            log.info("File uploaded to OSS: {}", fileUrl);
        } else {
            log.error("Failed to upload file to OSS: {}", fileName);
        }

        // 清理临时存储
        tempFileStorage.remove(fileName);
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
