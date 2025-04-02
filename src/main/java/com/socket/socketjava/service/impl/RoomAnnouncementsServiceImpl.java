package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.socket.socketjava.domain.pojo.RoomAnnouncements;
import com.socket.socketjava.mapper.RoomAnnouncementsMapper;
import com.socket.socketjava.service.IRoomAnnouncementsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomAnnouncementsServiceImpl extends ServiceImpl<RoomAnnouncementsMapper, RoomAnnouncements> implements IRoomAnnouncementsService {

    @Override
    @Transactional
    public void publishAnnouncement(Integer roomId, String content, String attachmentId, Integer isPinned, Integer userId) {
        RoomAnnouncements announcement = new RoomAnnouncements();
        announcement.setRoomId(roomId)
                .setContent(content)
                .setAttachmentUrls(attachmentId)
                .setIsPinned(isPinned)
                .setCreatorId(userId)
                .setStatus(1);
        save(announcement);
    }

    @Override
    @Transactional
    public void updateAnnouncement(Integer announcementId, String content, String attachmentUrls, Integer isPinned, Integer userId) {
        LambdaUpdateWrapper<RoomAnnouncements> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(RoomAnnouncements::getAnnouncementId, announcementId)
                .set(RoomAnnouncements::getContent, content)
                .set(RoomAnnouncements::getAttachmentUrls, attachmentUrls)
                .set(RoomAnnouncements::getIsPinned, isPinned)
                .set(RoomAnnouncements::getLastUpdaterId, userId);
        update(updateWrapper);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Integer announcementId, Integer status, Integer userId) {
        LambdaUpdateWrapper<RoomAnnouncements> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(RoomAnnouncements::getAnnouncementId, announcementId)
                .set(RoomAnnouncements::getStatus, status)
                .set(RoomAnnouncements::getLastUpdaterId, userId);
        update(updateWrapper);
    }

    @Override
    @Transactional
    public void pinAnnouncement(Integer announcementId, Integer isPinned, Integer userId) {
        LambdaUpdateWrapper<RoomAnnouncements> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(RoomAnnouncements::getAnnouncementId, announcementId)
                .set(RoomAnnouncements::getIsPinned, isPinned)
                .set(RoomAnnouncements::getLastUpdaterId, userId);
        update(updateWrapper);
    }

    @Override
    public Page<RoomAnnouncements> getAnnouncementPage(Integer roomId, Integer pageNum, Integer pageSize) {
        Page<RoomAnnouncements> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<RoomAnnouncements> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomAnnouncements::getRoomId, roomId)
                .eq(RoomAnnouncements::getStatus, 1)  // 只查询有效的公告
                .orderByDesc(RoomAnnouncements::getIsPinned)  // 置顶公告优先
                .orderByDesc(RoomAnnouncements::getUpdatedAt)  // 按更新时间降序
                .orderByDesc(RoomAnnouncements::getCreatedAt); // 按创建时间降序

        return page(page, queryWrapper);
    }
}