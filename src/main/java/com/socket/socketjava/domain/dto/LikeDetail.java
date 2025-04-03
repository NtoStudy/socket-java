package com.socket.socketjava.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDetail {

    @ApiModelProperty(value = "点赞ID")
    private Integer likeId;

    @ApiModelProperty(value = "动态ID")
    private Integer postId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "点赞时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户头像URL")
    private String avatarUrl;



}
