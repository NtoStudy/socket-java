package com.socket.socketjava.domain.vo.Users;

import lombok.Data;

@Data
public class UpdateInfoVo {
    private String username;
    private String avatarUrl;
    private String signature;
    private String hobbies;
    private String gender;
    private String status;
    private String customStatus;
    private String birthday;
    private String province;
    private String city;
}
