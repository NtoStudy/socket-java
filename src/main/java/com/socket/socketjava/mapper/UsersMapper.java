package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.menu.UserStatus;
import com.socket.socketjava.domain.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface UsersMapper extends BaseMapper<Users> {

    Users selectByNumber(String number);

    void updateStatus(UserStatus status, String number);

    void like(Integer friendId, Integer userId);
}
