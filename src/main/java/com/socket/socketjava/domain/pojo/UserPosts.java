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
 * 用户动态表
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_posts")
@ApiModel(value="UserPosts对象", description="用户动态表")
public class UserPosts implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "动态ID")
    @TableId(value = "post_id", type = IdType.AUTO)
    private Integer postId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "动态内容")
    private String content;

    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "发布时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "隐私设置")
    private String privacySetting;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDeleted;


}
