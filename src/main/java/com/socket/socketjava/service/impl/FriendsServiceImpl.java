package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.domain.pojo.Notifications;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.Friends.FriendVo;
import com.socket.socketjava.mapper.FriendsMapper;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.service.IFriendsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 好友关系表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends> implements IFriendsService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private NotificationsMapper notificationsMapper;
    @Autowired
    private FriendsMapper friendsMapper;

    @Override
    public void addFriend(String userNumber, String friendNumber) {
        // 先从users表中查到number对应的用户id
        Users user = usersMapper.selectByNumber(userNumber);
        Users friend = usersMapper.selectByNumber(friendNumber);

        // 检查用户是否存在
        if (user == null) {
            throw new IllegalArgumentException("用户 " + 1 + " 不存在");
        }
        if (friend == null) {
            throw new IllegalArgumentException("用户 " + 2 + " 不存在");
        }

        // 关系保存到friends表中，状态是待核审
        Friends friends = new Friends();
        friends.setUserId(user.getUserId());
        friends.setFriendId(friend.getUserId());
        friends.setStatus(0);
        this.save(friends);

        // 关系保存到系统通知表中，因为要通知对方
        notificationsMapper.insert(new Notifications()
                .setReceiverId(friend.getUserId())
                .setContent(user.getUsername() + " 请求加为好友")
                .setType("friend")
                .setStatus(0));
    }

    @Override
    public void acceptOrRejectFriend(Integer relationId, Integer status) {
        Friends friends = new Friends();
        friends.setRelationId(relationId);
        friends.setStatus(status);
        this.updateById(friends);
    }

    @Override
    public List<FriendVo> friendList(Integer userId) {
        return friendsMapper.selectFriendsList(userId);
    }


}
