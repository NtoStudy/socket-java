package com.socket.socketjava.domain.vo.Chatroom;

import lombok.Data;

import java.util.List;

@Data
public class CreateRoomVo {
    private String roomName;
    private List<Integer> userIds;
}