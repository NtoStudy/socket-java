package com.socket.socketjava.domain.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 动态媒体表
 * </p>
 *
 * @author 哞哞
 * @since 2025-04-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("post_media")
@ApiModel(value="PostMedia对象", description="动态媒体表")
public class PostMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "媒体ID")
    @TableId(value = "media_id", type = IdType.AUTO)
    private Integer mediaId;

    @ApiModelProperty(value = "动态ID")
    private Integer postId;

    @ApiModelProperty(value = "媒体类型")
    private String mediaType;

    @ApiModelProperty(value = "媒体URL")
    private String mediaUrl;


}
