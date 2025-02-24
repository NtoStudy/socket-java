package com.socket.socketjava.service;

import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.baomidou.mybatisplus.extension.service.IService;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;

import java.util.List;

/**
 * <p>
 * 用户-聊天室关联表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface IUserChatRoomsService extends IService<UserChatRooms> {

    String createChatRoom(Integer userId, CreateRoomVo createRoomVo);

    void acceptOrRejectChatRoom(Integer userId, Integer roomId, Integer status);

    List<ChatRoomListVo> getRoomList(Integer userId);

    List<Integer> getRoomUsers(Integer roomId);

    Integer getMessageCount(Integer userId, Integer roomId);

    void addGroup(Integer userId, String groupNumber);
}
