package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.socket.socketjava.mapper.UserChatRoomsMapper;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-聊天室关联表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class UserChatRoomsServiceImpl extends ServiceImpl<UserChatRoomsMapper, UserChatRooms> implements IUserChatRoomsService {

}
