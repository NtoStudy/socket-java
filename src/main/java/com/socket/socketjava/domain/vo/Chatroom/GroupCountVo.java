package com.socket.socketjava.domain.vo.Chatroom;


import com.socket.socketjava.domain.pojo.ChatRooms;
import lombok.Data;

import java.util.List;

@Data
public class GroupCountVo {
    private Integer groupCount;
    private List<ChatRooms> chatRoomsList;

}
