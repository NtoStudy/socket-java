package com.socket.socketjava.domain.holder;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class LoginHolder {
    private Integer userId;
    private String number;
    private String username;
}
