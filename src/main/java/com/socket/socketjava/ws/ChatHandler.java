package com.socket.socketjava.ws;

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
import com.socket.socketjava.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
@RestController
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
    @Autowired
    private RedisUtils redisUtils;

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

                // 清除相关缓存
                clearChatHistoryCache(messagesToSave.getSenderId(), messagesToSave.getReceiverId());

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

    @PostMapping("/upload/chunk")
    public ResponseEntity<String> uploadFileChunk(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("fileName") String fileName,
                                                  @RequestParam("chunkNumber") int chunkNumber,
                                                  @RequestParam("totalChunks") int totalChunks) {
        try {
            // 保存文件分片到临时存储
            tempFileStorage.computeIfAbsent(fileName, k -> new ConcurrentHashMap<>()).put(chunkNumber, file.getBytes());
            log.info("Chunk {} of {} uploaded for file: {}", chunkNumber, totalChunks, fileName);

            // 判断是否所有分片都已上传完成
            if (chunkNumber == totalChunks - 1) {
                // 合并文件分片
                rebuildFile(fileName, totalChunks);
            }

            return ResponseEntity.ok("Chunk uploaded successfully");
        } catch (Exception e) {
            log.error("Error while uploading chunk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    /**
     * 重组文件
     */
    private void rebuildFile(String fileName, int totalChunks) {
        Map<Integer, byte[]> chunks = tempFileStorage.get(fileName);
        if (chunks == null || chunks.size() != totalChunks) {
            log.error("File chunks incomplete for file: {}", fileName);
            return;
        }

        // 按分片编号排序并合并
        Map<Integer, byte[]> sortedChunks = new TreeMap<>(chunks);
        byte[] fileData = new byte[0];
        for (byte[] chunk : sortedChunks.values()) {
            byte[] newFileData = new byte[fileData.length + chunk.length];
            System.arraycopy(fileData, 0, newFileData, 0, fileData.length);
            System.arraycopy(chunk, 0, newFileData, fileData.length, chunk.length);
            fileData = newFileData;
        }

        log.info("File reconstructed successfully: {}", fileName);
        uploadToOss(fileData, fileName);
        tempFileStorage.remove(fileName); // 清理临时存储
    }

    private void uploadToOss(byte[] fileData, String fileName) {
        String objectName = "uploads/" + UUID.randomUUID() + "-" + fileName;
        String fileUrl = aliyunOssOperator.uploadBytes(fileData, objectName);

        if (fileUrl != null) {
            log.info("File uploaded to OSS: {}", fileUrl);
            // 保存文件URL到数据库或返回给前端
            notifyClient(fileUrl, fileName);

        } else {
            log.error("Failed to upload file to OSS: {}", fileName);
        }
    }

    private void notifyClient(String fileUrl, String fileName) {
        onlineUsers.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    // 构造消息对象
                    Map<String, String> message = new HashMap<>();
                    message.put("type", "file-upload-complete");
                    message.put("fileName", fileName);
                    message.put("fileUrl", fileUrl);

                    // 转换为 JSON 并发送
                    String jsonMessage = objectMapper.writeValueAsString(message);
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    log.error("Error sending WebSocket message", e);
                }
            }
        });
    }

    /**
     * 清除聊天历史缓存
     */
    private void clearChatHistoryCache(Integer userId, Integer receiverId) {
        // 清除发送方缓存
        String senderKeyPattern = String.format("chat:history:%d:%d:*", userId, receiverId);
        redisUtils.deleteByPattern(senderKeyPattern);

        // 清除接收方缓存
        String receiverKeyPattern = String.format("chat:history:%d:%d:*", receiverId, userId);
        redisUtils.deleteByPattern(receiverKeyPattern);
    }

}
