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
 * 聊天消息表
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("messages")
@ApiModel(value="Messages对象", description="聊天消息表")
public class Messages implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息唯一标识")
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;

    @ApiModelProperty(value = "发送者ID")
    private Integer senderId;

    @ApiModelProperty(value = "接收者ID")
    private Integer receiverId;

    @ApiModelProperty(value="群聊Id")
    private Integer chatRoomId;

    @ApiModelProperty(value = "消息发送状态")
    private String messageType;

    @ApiModelProperty(value = "消息类型")
    private String type;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sentTime;

    @ApiModelProperty(value = "消息是否已读")
    private Integer isRead;

    @ApiModelProperty(value = "发送者是否删除该消息")
    private Integer deletedBySender;

    @ApiModelProperty(value = "接收者是否删除该消息")
    private Integer deletedByReceiver;

}
