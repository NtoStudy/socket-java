package com.socket.socketjava.domain.dto;

import lombok.Data;

@Data
public class GroupUserInfo {
    private Integer userId;
    private String username;
    private String avatar;
    private String number;
    private String nickname;
    private String role;
}
