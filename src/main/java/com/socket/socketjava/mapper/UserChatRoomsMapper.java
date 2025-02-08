package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;

import java.util.List;

/**
 * <p>
 * 用户-聊天室关联表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface UserChatRoomsMapper extends BaseMapper<UserChatRooms> {


    List<ChatRoomListVo> selectRoomList(Integer userId);
}
