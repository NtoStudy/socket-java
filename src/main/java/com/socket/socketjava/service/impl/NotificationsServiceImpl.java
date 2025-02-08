package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.domain.pojo.Notifications;
import com.socket.socketjava.domain.vo.Notifications.AcceptFriendVo;
import com.socket.socketjava.domain.vo.Notifications.AcceptRoomsVo;
import com.socket.socketjava.mapper.ChatRoomsMapper;
import com.socket.socketjava.mapper.FriendsMapper;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.service.INotificationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
@Slf4j
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications> implements INotificationsService {

    @Autowired
    private NotificationsMapper notificationsMapper;
    @Autowired
    private FriendsMapper friendsMapper;
    @Autowired
    private ChatRoomsMapper chatRoomsMapper;

    @Override
    public List<AcceptFriendVo> selectFriend(Integer userId) {
        // 从这里可以查到relationId userId username number avatarUrl status
        List<AcceptFriendVo> acceptFriendVoList = friendsMapper.selectByReceiverId(userId);
        for (AcceptFriendVo acceptFriendVo : acceptFriendVoList) {
            acceptFriendVo.setStatus(1);
        }
        return acceptFriendVoList;
    }

    @Override
    public List<AcceptRoomsVo> selectRooms(Integer userId) {
        // 这里能获得roomName creatorId avatarUrl status content
        return chatRoomsMapper.selectByCreatorId(userId);
    }
}
