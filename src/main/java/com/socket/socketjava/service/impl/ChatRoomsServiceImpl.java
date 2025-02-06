package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.mapper.ChatRoomsMapper;
import com.socket.socketjava.service.IChatRoomsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 聊天室表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class ChatRoomsServiceImpl extends ServiceImpl<ChatRoomsMapper, ChatRooms> implements IChatRoomsService {

}
