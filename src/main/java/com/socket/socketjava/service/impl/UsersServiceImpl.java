package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

}
