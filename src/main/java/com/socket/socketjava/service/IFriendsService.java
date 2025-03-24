package com.socket.socketjava.service;

import com.socket.socketjava.domain.pojo.Friends;
import com.baomidou.mybatisplus.extension.service.IService;
import com.socket.socketjava.domain.vo.Friends.FriendVo;

import java.util.List;

/**
 * <p>
 * 好友关系表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface IFriendsService extends IService<Friends> {

    void addFriend(String userNumber, String friendNumber);


    void acceptOrRejectFriend(Integer relationId, Integer status);

    List<FriendVo> friendList(Integer userId);

    Integer getMessageCount(Integer userId, Integer relationId);

    Integer userIsFriend(Integer userId, Integer friendId);

    boolean togglePinFriend(Integer userId, Integer friendId, Integer status);

    boolean setFriendRemark(Integer userId, Integer friendId, String remark);

    String handleFriendRequest(Integer relationId, Integer status);
}
