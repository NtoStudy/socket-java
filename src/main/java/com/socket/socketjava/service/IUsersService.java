package com.socket.socketjava.service;

import com.socket.socketjava.domain.menu.UserStatus;
import com.socket.socketjava.domain.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.socket.socketjava.domain.vo.Users.LoginVo;
import com.socket.socketjava.domain.vo.Users.RegisterVo;


/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface IUsersService extends IService<Users> {

    String login(LoginVo loginVo);

    String register(RegisterVo registerVo);

    void changeStatus(UserStatus status, String number);

}
