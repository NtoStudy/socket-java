package com.socket.socketjava.service;

import com.socket.socketjava.domain.pojo.Notifications;
import com.baomidou.mybatisplus.extension.service.IService;
import com.socket.socketjava.domain.vo.Notifications.AcceptFriendVo;
import com.socket.socketjava.domain.vo.Notifications.AcceptRoomsVo;

import java.util.List;

/**
 * <p>
 * 系统通知表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface INotificationsService extends IService<Notifications> {

    List<AcceptFriendVo> selectFriend(Integer userId);

    List<AcceptRoomsVo> selectRooms(Integer userId);

    long countUnhandledFriendRequests(Integer userId);

    long countUnhandledRoomInvitations(Integer userId);
}
