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
 * 动态点赞表
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("post_likes")
@ApiModel(value="PostLikes对象", description="动态点赞表")
public class PostLikes implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "点赞ID")
    @TableId(value = "like_id", type = IdType.AUTO)
    private Integer likeId;

    @ApiModelProperty(value = "动态ID")
    private Integer postId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "点赞时间")
    private LocalDateTime createdAt;


}
