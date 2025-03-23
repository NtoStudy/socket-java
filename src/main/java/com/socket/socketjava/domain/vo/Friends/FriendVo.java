package com.socket.socketjava.domain.vo.Friends;

import lombok.Data;

@Data
public class FriendVo {

    private Integer relationId;

    private Integer friendId;

    private Integer isPinned;

    private Integer relationStatus;

    private String username;

    private String number;

    private  String avatarUrl;

    private String userStatus;
}
