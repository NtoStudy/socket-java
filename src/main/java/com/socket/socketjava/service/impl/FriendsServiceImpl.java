package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

        // 关系保存到friends表中，状态是待核审
        Friends friends = new Friends();
        friends.setUserId(user.getUserId());
        friends.setFriendId(friend.getUserId());
        friends.setStatus(0);
        this.save(friends);

        // 关系保存到系统通知表中，因为要通知对方
        notificationsMapper.insert(new Notifications()
                .setReceiverId(friend.getUserId())
                .setRelatedId(friends.getRelationId())
                .setContent(user.getUsername() + " 请求加为好友")
                .setType("friend")
                .setStatus(0));
    }

    @Override
    public void acceptOrRejectFriend(Integer relationId, Integer status) {

        Friends friends = getById(relationId);
        friends.setStatus(status);
        updateById(friends);


        if (status == 1) {
            // 代表同意，直接插入两条数据
            Integer userId = friends.getUserId();
            Integer friendId = friends.getFriendId();

            Friends newFriend = new Friends();
            newFriend.setUserId(friendId);
            newFriend.setFriendId(userId);
            newFriend.setStatus(1);
            // TODO后续把默认分组搞定之后，插入分组
            LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            friendsLambdaQueryWrapper
                    .eq(Friends::getUserId, friendId)
                    .eq(Friends::getFriendId, userId);
            Friends one = getOne(friendsLambdaQueryWrapper);

            if (one != null) {
                // 证明已经存在了，更新状态
                one.setStatus(1);
                updateById(one);
            } else {
                save(newFriend);
            }
        }
    }


    @Override
    public List<FriendVo> friendList(Integer userId) {
        return friendsMapper.selectFriendsList(userId);
    }

    @Override
    public Integer getMessageCount(Integer userId, Integer relationId) {
        // 在这里的查询有问题，因为我的userId是固定的，所以要从relationId去判断一下
        Integer friendId = friendsMapper.getFriendByRelationId(userId, relationId);
        Integer count = friendsMapper.getMessageCount(userId, friendId);
        if (count <= 99) return count;
        else return 100;
    }

    @Override
    public Integer userIsFriend(Integer userId, Integer friendId) {
        return friendsMapper.userIsFriend(userId, friendId);
    }

    @Override
    public boolean togglePinFriend(Integer userId, Integer friendId, Integer status) {
        // 查询当前好友关系
        LambdaUpdateWrapper<Friends> friendsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        friendsLambdaUpdateWrapper.eq(Friends::getFriendId, friendId)
                .eq(Friends::getUserId, userId)
                .set(Friends::getIsPinned, status);
        update(friendsLambdaUpdateWrapper);

        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper
                .eq(Friends::getFriendId, friendId)
                .eq(Friends::getUserId, userId);
        Friends one = getOne(friendsLambdaQueryWrapper);

        return one.getIsPinned() == 1;
    }

    @Override
    public boolean setFriendRemark(Integer userId, Integer friendId, String remark) {
        return update()
                .eq("user_id", userId)
                .eq("friend_id", friendId)
                .set("remark", remark)
                .update();
    }

    @Override
    public String handleFriendRequest(Integer relationId, Integer status) {
        acceptOrRejectFriend(relationId, status);

        if (status == 1) return "已接受";
        else if (status == 2) return "已拒绝";
        return "服务异常";
    }


}
