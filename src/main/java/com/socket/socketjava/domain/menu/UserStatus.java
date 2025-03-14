package com.socket.socketjava.domain.menu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserStatus {
    ONLINE("online"),
    OFFLINE("offline"),
    BUSY("busy"),
    AWAY("away"),
    DND("dnd");

    @EnumValue
    private final String status;

    UserStatus(String status) {
        this.status = status;
    }


}
