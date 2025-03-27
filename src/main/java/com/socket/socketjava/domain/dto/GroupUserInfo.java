package com.socket.socketjava.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupUserInfo {
    private Integer userId;
    private String username;
    private String number;
    private String nickname;
    private String role;
    @ApiModelProperty(value = "用户头像URL")
    private String avatarUrl;
    @ApiModelProperty(value = "用户状态")
    private String status;
    @ApiModelProperty(value = "用户自定义状态")
    private String customStatus;
    @ApiModelProperty(value = "兴趣爱好")
    private String hobbies;
    @ApiModelProperty(value = "被点赞的个数")
    private Integer likeCount;
}
