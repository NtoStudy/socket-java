package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.LoginVo;
import com.socket.socketjava.domain.vo.RegisterVo;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.result.ResultCodeEnum;
import com.socket.socketjava.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

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

    @Autowired
    private UsersMapper usersMapper;


    @Override
    public String login(LoginVo loginVo) {
        // 先判断用户名number是否存在


        // 再判断密码是否正确


        return "";
    }

    @Override
    public String register(RegisterVo registerVo) {
        // 实现思路：输入用户名username,输入密码password，
        // 随机生成十位数字写入数据库users表中的username,password,number字段。

        // 将registerVo转换为Users
        Users users = new Users();
        users.setUsername(registerVo.getUsername());
        users.setPassword(registerVo.getPassword());
        users.setNumber(generateUniqueNumber());
        this.save(users);
        return users.getNumber();
    }

    /**
     * 随机生成十位数字
     * @return
     */

    public String generateRandomNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // 生成第一个数字，确保它不是0
        int firstDigit = random.nextInt(9) + 1; // 生成1到9之间的数字
        sb.append(firstDigit);

        // 生成剩余的9个数字
        for (int i = 1; i < 10; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    /**
     * 判断生成的十位数字数据库中是否存在
     * @param number
     * @return
     */
    public boolean isNumberExists(String number) {
        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Users::getNumber, number);
        return usersMapper.selectOne(wrapper) != null;
    }

    /**
     * 生成唯一十位数字
     * @return
     */
    public String generateUniqueNumber() {
        String number;
        do {
            number = generateRandomNumber();
        } while (isNumberExists(number));
        return number;
    }
}
