package com.socket.socketjava.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.socket.socketjava.domain.pojo.RoomAnnouncements;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 群公告表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-29
 */
public interface IRoomAnnouncementsService extends IService<RoomAnnouncements> {

    void publishAnnouncement(Integer roomId, String content, String attachmentId, Integer isPinned, Integer userId);

    void updateAnnouncement(Integer announcementId, String content, String attachmentUrls, Integer isPinned, Integer userId);

    void deleteAnnouncement( Integer announcementId, Integer status, Integer userId);

    void pinAnnouncement( Integer announcementId, Integer isPinned, Integer userId);

    Page<RoomAnnouncements> getAnnouncementPage(Integer roomId, Integer pageNum, Integer pageSize);
}
