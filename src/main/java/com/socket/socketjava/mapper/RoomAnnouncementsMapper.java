package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.RoomAnnouncements;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 群公告表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-29
 */
public interface RoomAnnouncementsMapper extends BaseMapper<RoomAnnouncements> {

    List<RoomAnnouncements> getAnnouncement(Integer roomId);
}
