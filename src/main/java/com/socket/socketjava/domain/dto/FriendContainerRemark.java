package com.socket.socketjava.domain.dto;

import com.socket.socketjava.domain.pojo.Users;
import lombok.Data;

@Data
public class FriendContainerRemark {
    private String remark;
    private Users users;
}
