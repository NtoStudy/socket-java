package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.Friends;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.socket.socketjava.domain.vo.Friends.FriendVo;
import com.socket.socketjava.domain.vo.Notifications.AcceptFriendVo;

import java.util.List;

/**
 * <p>
 * 好友关系表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface FriendsMapper extends BaseMapper<Friends> {

    List <AcceptFriendVo> selectByReceiverId(Integer userId);

    List<FriendVo> selectFriendsList(Integer userId);


    Integer getMessageCount(Integer userId, Integer friendId);


    Integer getFriendByRelationId(Integer userId, Integer relationId);

    Integer userIsFriend(Integer userId, Integer friendId);
}
