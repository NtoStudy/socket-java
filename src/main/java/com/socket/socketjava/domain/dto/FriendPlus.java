package com.socket.socketjava.domain.dto;

import com.socket.socketjava.domain.pojo.Users;
import lombok.Data;

@Data
public class FriendPlus {
    private String remark;
    private Integer isPinned;
    private Users users;
}
