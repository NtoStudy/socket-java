package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.domain.pojo.Notifications;
import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
import com.socket.socketjava.mapper.ChatRoomsMapper;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.mapper.UserChatRoomsMapper;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户-聊天室关联表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
@Slf4j
public class UserChatRoomsServiceImpl extends ServiceImpl<UserChatRoomsMapper, UserChatRooms> implements IUserChatRoomsService {

    @Autowired
    private UserChatRoomsMapper userChatRoomsMapper;
    @Autowired
    private ChatRoomsMapper chatRoomsMapper;
    @Autowired
    private NotificationsMapper notificationsMapper;
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public void createChatRoom(Integer userId, CreateRoomVo createRoomVo) {
        // 新建一个聊天室 插入到聊天室表
        ChatRooms chatRooms = new ChatRooms();
        chatRooms.setRoomName(createRoomVo.getRoomName());
        chatRooms.setCreatorId(userId);
        chatRoomsMapper.insert(chatRooms);
        // 通过用户的id获取用户的username
        Integer roomId = chatRooms.getRoomId();
        String username = usersMapper.selectById(userId).getUsername();
        // 先将用户自己插入到聊天室内
        UserChatRooms userChatRooms1 = new UserChatRooms();
        userChatRooms1.setUserId(userId);
        userChatRooms1.setRoomId(roomId);
        userChatRooms1.setStatus(1);
        userChatRoomsMapper.insert(userChatRooms1);
        // 拿到聊天室的id 插入用户聊天室表中
        for (Integer userIds : createRoomVo.getUserIds()) {
            UserChatRooms userChatRooms = new UserChatRooms();
            userChatRooms.setUserId(userIds);
            userChatRooms.setRoomId(roomId);
            userChatRoomsMapper.insert(userChatRooms);
            // 要将邀请信息插入到系统通知表中，以便用户得知信息
            notificationsMapper.insert(
                    new Notifications()
                            .setReceiverId(userIds)
                            .setRelatedId(roomId)
                            .setContent(username + "邀请你加入群聊" + createRoomVo.getRoomName())
                            .setType("chatroom")
                            .setStatus(0)
            );
        }
    }

    @Override
    public void acceptOrRejectChatRoom(Integer userId, Integer roomId, Integer status) {
        // 这里的userId代指的是 "登录者的id也就是notifications表中的receiverId"
        // roomId关联user_chat_rooms表中的room_id来查询出聊天室信息，并且userId和这个表中的user_id相同然后修改状态
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .set(UserChatRooms::getStatus, status);
        this.update(userChatRoomsLambdaUpdateWrapper);
    }

    @Override
    public List<ChatRoomListVo> getRoomList(Integer userId) {
        return userChatRoomsMapper.selectRoomList(userId);
    }
}