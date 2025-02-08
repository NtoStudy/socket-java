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
import org.springframework.stereotype.Component;

/**
 * <p>
 * 聊天室表
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_rooms")
@ApiModel(value="ChatRooms对象", description="聊天室表")
public class ChatRooms implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "聊天室唯一标识")
    @TableId(value = "room_id", type = IdType.AUTO)
    private Integer roomId;

    @ApiModelProperty(value = "聊天室名称")
    private String roomName;

    @ApiModelProperty(value = "聊天室头像")
    private String avatarUrl;

    @ApiModelProperty(value = "创建者ID")
    private Integer creatorId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;


}
