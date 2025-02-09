package com.socket.socketjava.domain;

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
 * 群聊消息表
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("group_messages")
@ApiModel(value="GroupMessages对象", description="群聊消息表")
public class GroupMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息唯一标识")
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;

    @ApiModelProperty(value = "发送者ID")
    private Integer senderId;

    @ApiModelProperty(value = "聊天室ID")
    private Integer chatRoomId;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sentTime;

    @ApiModelProperty(value = "消息是否已读")
    private Integer isRead;

    @ApiModelProperty(value = "管理员是否删除该消息")
    private Integer deletedByAdmin;

    @ApiModelProperty(value = "删除该消息的用户ID列表")
    private String deletedByUsers;


}
