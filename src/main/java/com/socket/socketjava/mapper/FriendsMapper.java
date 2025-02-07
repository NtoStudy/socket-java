package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.Friends;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.socket.socketjava.domain.vo.AcceptFriendVo;

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

    AcceptFriendVo selectByReceiverId(Integer userId);
}
