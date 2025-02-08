package com.socket.socketjava.domain.vo.Notifications;

import lombok.Data;

@Data
public class AcceptRoomsVo {
    private Integer roomId;
    private String roomName;
    private Integer creatorId;
    private String avatarUrl;
    private Integer status;
    private String content;
}
