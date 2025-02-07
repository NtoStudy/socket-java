package com.socket.socketjava.domain.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AcceptFriendVo {

    @ApiModelProperty(value = "关系id")
    private Integer relationId;

    @ApiModelProperty(value = "好友id")
    private Integer userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "随机分配的十位数字")
    private String number;

    @ApiModelProperty(value = "用户头像URL")
    private String avatarUrl;

    @ApiModelProperty(value = "好友关系状态,0未接受，1已接受，2已拒绝")
    private Integer status;

    @ApiModelProperty(value = "通知内容")
    private String content;
}
