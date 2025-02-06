package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.LoginVo;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.result.ResultCodeEnum;
import com.socket.socketjava.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public String login(LoginVo loginVo) {
        // 先判断用户名number是否存在



        // 再判断密码是否正确


        return "";
    }
}
