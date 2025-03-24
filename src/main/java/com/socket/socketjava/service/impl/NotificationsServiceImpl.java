package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
    private FriendsMapper friendsMapper;
    @Autowired
    private ChatRoomsMapper chatRoomsMapper;

    @Override
    public List<AcceptFriendVo> selectFriend(Integer userId) {
        // 从这里可以查到relationId userId username number avatarUrl status
        List<AcceptFriendVo> acceptFriendVoList = friendsMapper.selectByReceiverId(userId);
        // 将notifications表中的状态改为1,表示已经查过
        LambdaUpdateWrapper<Notifications> notificationsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        notificationsLambdaUpdateWrapper
                .eq(Notifications::getReceiverId, userId)
                .set(Notifications::getStatus, 1);
        this.update(notificationsLambdaUpdateWrapper);

        return acceptFriendVoList;
    }

    @Override
    public List<AcceptRoomsVo> selectRooms(Integer userId) {
        // 这里能获得roomName creatorId avatarUrl status content
        LambdaUpdateWrapper<Notifications> notificationsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        notificationsLambdaUpdateWrapper
                .eq(Notifications::getReceiverId, userId)
                .set(Notifications::getStatus, 1);
        this.update(notificationsLambdaUpdateWrapper);
        List<AcceptRoomsVo> acceptRoomsVos = chatRoomsMapper.selectByCreatorId(userId);
        List<AcceptRoomsVo> acceptRoomsVoss = chatRoomsMapper.selectManager(userId);

        // 将acceptRoomsVos和acceptRoomsVoss合并
        if (acceptRoomsVos != null && acceptRoomsVoss != null) {
            acceptRoomsVos.addAll(acceptRoomsVoss);
        } else if (acceptRoomsVos == null) {
            acceptRoomsVos = acceptRoomsVoss;
        }

        return acceptRoomsVos;
    }

    @Override
    public long countUnhandledFriendRequests(Integer userId) {
        LambdaQueryWrapper<Notifications> notificationsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        notificationsLambdaQueryWrapper
                .eq(Notifications::getReceiverId, userId)
                .eq(Notifications::getStatus, 0)
                .eq(Notifications::getType, "friend");
        return count(notificationsLambdaQueryWrapper);
    }

    @Override
    public long countUnhandledRoomInvitations(Integer userId) {
        LambdaQueryWrapper<Notifications> notificationsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        notificationsLambdaQueryWrapper
                .eq(Notifications::getReceiverId, userId)
                .eq(Notifications::getStatus, 0)
                .eq(Notifications::getType, "chatroom");
        return count(notificationsLambdaQueryWrapper);
    }
}
