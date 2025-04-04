package com.socket.socketjava.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

@Data
public class PostDetail {
    @ApiModelProperty(value = "动态ID")
    private Integer postId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "动态内容")
    private String content;

    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    @ApiModelProperty(value = "发布时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "是否置顶（1: 是, 0: 否）")
    private Integer isPinned;

    @ApiModelProperty(value = "媒体ID")
    @TableId(value = "media_id", type = IdType.AUTO)
    private Integer mediaId;

    @ApiModelProperty(value = "媒体类型")
    private String mediaType;

    @ApiModelProperty(value = "媒体URL")
    private String mediaUrl;

}
