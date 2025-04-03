package com.socket.socketjava.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommentDetail {

    @ApiModelProperty(value = "评论ID")
    private Integer commentId;

    @ApiModelProperty(value = "动态ID")
    private Integer postId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "父评论ID")
    private Integer parentCommentId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户头像URL")
    private String avatarUrl;



}
