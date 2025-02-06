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
 * 用户表
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("users")
@ApiModel(value="Users对象", description="用户表")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户唯一标识")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "随机分配的十位数字")
    private String number;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "用户头像URL")
    private String avatarUrl;

    @ApiModelProperty(value = "注册时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "用户状态")
    private Integer status;


}
