package com.socket.socketjava.domain.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户-聊天室关联表
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_chat_rooms")
@ApiModel(value="UserChatRooms对象", description="用户-聊天室关联表")
public class UserChatRooms implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户-聊天室关系唯一标识")
    @TableId(value = "user_chat_room_id", type = IdType.AUTO)
    private Integer userChatRoomId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "聊天室ID")
    private Integer roomId;

    @ApiModelProperty(value = "加入时间")
    private LocalDateTime joinedAt;

    @ApiModelProperty(value = "用户在聊天室中的状态")
    private Integer status;

    @ApiModelProperty(value = "角色类型")
    private String role;

    @ApiModelProperty(value = "是否置顶")
    private Boolean isPinned;

    @ApiModelProperty(value = "禁言截止时间")
    private LocalDateTime mutedUntil;


}
