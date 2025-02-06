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
 * 系统通知表
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notifications")
@ApiModel(value="Notifications对象", description="系统通知表")
public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "通知唯一标识")
    @TableId(value = "notification_id", type = IdType.AUTO)
    private Integer notificationId;

    @ApiModelProperty(value = "接收者ID")
    private Integer receiverId;

    @ApiModelProperty(value = "通知内容")
    private String content;

    @ApiModelProperty(value = "通知类型")
    private String type;

    @ApiModelProperty(value = "通知状态")
    private Integer status;

    @ApiModelProperty(value = "通知创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "关联的记录ID")
    private Integer relatedId;


}
