package com.socket.socketjava.domain.vo.Chatroom;

import lombok.Data;

@Data
public class ChatRoomListVo {
    private Integer roomId;

    private Integer userId;

    private String roomName;

    private String avatarUrl;

}
