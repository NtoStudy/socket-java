package com.socket.socketjava.domain.dto;

import com.socket.socketjava.domain.pojo.ChatRooms;
import lombok.Data;

@Data
public class GroupPlus {
    private ChatRooms chatRooms;
    private Integer isPinned;
}
