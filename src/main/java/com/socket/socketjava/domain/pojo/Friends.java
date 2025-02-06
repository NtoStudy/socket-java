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
 * 好友关系表
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("friends")
@ApiModel(value="Friends对象", description="好友关系表")
public class Friends implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "好友关系唯一标识")
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Integer relationId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "好友ID")
    private Integer friendId;

    @ApiModelProperty(value = "好友关系状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;


}
