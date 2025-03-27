package com.socket.socketjava.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupPlus {
    private Integer isPinned;
    private String nickname;
    private String role;


    @TableId(value = "room_id", type = IdType.AUTO)
    private Integer roomId;

    @ApiModelProperty(value = "聊天室名称")
    private String roomName;

    @ApiModelProperty(value = "创建者ID")
    private Integer creatorId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "聊天室头像")
    private String avatarUrl;

    @ApiModelProperty(value = "群组编号")
    private String groupNumber;

    @ApiModelProperty(value = "群公告")
    private String announcement;

    @ApiModelProperty(value = "置顶消息ID")
    private Integer pinnedMessageId;

}
