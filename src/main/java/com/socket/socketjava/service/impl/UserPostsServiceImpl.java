package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.UserPosts;
import com.socket.socketjava.mapper.UserPostsMapper;
import com.socket.socketjava.service.IUserPostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户动态表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@Service
public class UserPostsServiceImpl extends ServiceImpl<UserPostsMapper, UserPosts> implements IUserPostsService {

}
