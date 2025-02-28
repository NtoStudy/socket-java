package com.socket.socketjava.domain.dto;

import lombok.Data;

@Data
public class GroupIsContainerUser {

    private Integer isContainer; // 0代表不在群聊内，1代表在群聊内
    private Integer roomId;
    private String roomName;
    private String avatarUrl;
    private String groupNumber;
}
