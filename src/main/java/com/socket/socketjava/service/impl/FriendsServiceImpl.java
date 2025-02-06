package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.mapper.FriendsMapper;
import com.socket.socketjava.service.IFriendsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
