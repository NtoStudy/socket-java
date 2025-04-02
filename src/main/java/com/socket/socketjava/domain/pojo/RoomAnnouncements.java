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
 * 群公告表
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("room_announcements")
@ApiModel(value="RoomAnnouncements对象", description="群公告表")
public class RoomAnnouncements implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公告唯一标识")
    @TableId(value = "announcement_id", type = IdType.AUTO)
    private Integer announcementId;

    @ApiModelProperty(value = "关联的聊天室ID")
    private Integer roomId;

    @ApiModelProperty(value = "公告内容（文本）")
    private String content;

    @ApiModelProperty(value = "发布公告的用户ID")
    private Integer creatorId;

    @ApiModelProperty(value = "公告发布时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "公告最后更新时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "最后更新公告的用户ID")
    private Integer lastUpdaterId;

    @ApiModelProperty(value = "公告状态（1: 有效, 0: 已撤回, 2: 已删除）")
    private Integer status;

    @ApiModelProperty(value = "公告附件（图片、文件等URL，逗号分隔）")
    private String attachmentUrls;

    @ApiModelProperty(value = "是否置顶（1: 是, 0: 否）")
    private Integer isPinned;


}
