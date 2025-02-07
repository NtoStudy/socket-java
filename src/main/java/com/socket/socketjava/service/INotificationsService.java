package com.socket.socketjava.service;

import com.socket.socketjava.domain.pojo.Notifications;
import com.baomidou.mybatisplus.extension.service.IService;
import com.socket.socketjava.domain.vo.AcceptFriendVo;

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
}
