package com.socket.socketjava.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVo {
    private String number;
    private String password;
}
