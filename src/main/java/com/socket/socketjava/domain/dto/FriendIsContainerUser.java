package com.socket.socketjava.domain.dto;


import lombok.Data;

@Data
public class FriendIsContainerUser {
    private Integer isContainer;
    private Integer isUser;
    private String avatarUrl;
    private String userName;
    private Integer userId;
    private String number;
}
