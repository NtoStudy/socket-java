package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.domain.pojo.Notifications;
import com.socket.socketjava.domain.vo.AcceptFriendVo;
import com.socket.socketjava.mapper.FriendsMapper;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.service.INotificationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统通知表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications> implements INotificationsService {

    @Autowired
    private NotificationsMapper notificationsMapper;
    @Autowired
    private FriendsMapper friendsMapper;

    @Override
    public List<AcceptFriendVo> selectFriend(Integer userId) {
        // 通过接收者id查询
        // 此时 acceptFriendVo 只有content,其他内容应该从friend表中获取
        Notifications notifications = notificationsMapper.selectByReceiverId(userId);
        // 从这里可以查到relationId userId username number avatarUrl status
        AcceptFriendVo acceptFriendVo = friendsMapper.selectByReceiverId(userId);
        acceptFriendVo.setContent(notifications.getContent());
        return List.of(acceptFriendVo);
    }
}
