package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.dto.FriendIsContainerUser;
import com.socket.socketjava.domain.dto.FriendPlus;
import com.socket.socketjava.domain.menu.UserStatus;
import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.Users.LoginVo;
import com.socket.socketjava.domain.vo.Users.RegisterVo;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.result.ResultCodeEnum;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.socket.socketjava.utils.utils.JwtUtil;
import com.socket.socketjava.utils.exception.socketException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
    @Autowired
    private IFriendsService friendsService;


    @Override
    public String login(LoginVo loginVo) {
        Users users = usersMapper.selectByNumber(loginVo.getNumber());
        // 先判断用户名number是否存在
        if (users == null) {
            throw new socketException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        // 再判断密码是否正确
        if (!users.getPassword().equals(loginVo.getPassword())) {
            throw new socketException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        // 将用户状态更改为在线
        usersMapper.updateStatus(UserStatus.ONLINE, loginVo.getNumber());

        // 如果所有对实现，则创建jwt令牌
        return JwtUtil.createToken(users.getUserId(), users.getNumber());
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


    @Override
    public void changeStatus(UserStatus status, String number) {
        usersMapper.updateStatus(status, number);
    }

    @Override
    public void like(Integer friendId, Integer userId) {
        usersMapper.like(friendId, userId);
    }

    @Override
    public FriendIsContainerUser getUserInfoByNumber(String number, Integer currentUserId) {
        FriendIsContainerUser friendIsContainerUser = new FriendIsContainerUser();

        // 查询用户信息
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getNumber, number);
        Users users = getOne(usersLambdaQueryWrapper);

        // 设置基本信息
        friendIsContainerUser.setUserName(users.getUsername());
        friendIsContainerUser.setAvatarUrl(users.getAvatarUrl());
        friendIsContainerUser.setNumber(users.getNumber());
        friendIsContainerUser.setUserId(users.getUserId());

        // 判断是否是自己
        Integer userId = users.getUserId();
        if (Objects.equals(userId, currentUserId)) {
            friendIsContainerUser.setIsUser(1);
        } else {
            friendIsContainerUser.setIsUser(0);
        }

        // 判断是否是好友
        Integer userIsFriend = friendsService.userIsFriend(currentUserId, userId);
        if (userIsFriend == 1) {
            friendIsContainerUser.setIsContainer(1);
        } else {
            friendIsContainerUser.setIsContainer(0);
        }

        return friendIsContainerUser;
    }

    @Override
    public FriendPlus getUserInfoWithFriendRelation(Integer userId, Integer currentUserId) {
        // 获取用户信息
        Users users = getById(userId);

        // 获取好友关系信息
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getFriendId, userId)
                .eq(Friends::getUserId, currentUserId);
        Friends friend = friendsService.getOne(friendsLambdaQueryWrapper);

        // 组装返回数据
        FriendPlus friendPlus = new FriendPlus();
        friendPlus.setRemark(friend.getRemark());
        friendPlus.setIsPinned(friend.getIsPinned());
        friendPlus.setUsername(users.getUsername());
        friendPlus.setNumber(users.getNumber());
        friendPlus.setAvatarUrl(users.getAvatarUrl());
        friendPlus.setStatus(users.getStatus());
        friendPlus.setCustomStatus(users.getCustomStatus());
        friendPlus.setBirthday(users.getBirthday());
        friendPlus.setHobbies(users.getHobbies());
        friendPlus.setSignature(users.getSignature());
        friendPlus.setGender(users.getGender());
        friendPlus.setProvince(users.getProvince());
        friendPlus.setCity(users.getCity());
        friendPlus.setLikeCount(users.getLikeCount());
        friendPlus.setUserId(users.getUserId());





        return friendPlus;
    }

    @Override
    public void updateUserInfo(Users users, Integer userId) {
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getUserId, userId);
        update(users, usersLambdaQueryWrapper);
    }


    /**
     * 随机生成首位不为0的十位数字
     *
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
     *
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
     *
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
