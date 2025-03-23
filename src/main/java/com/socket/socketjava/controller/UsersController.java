package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.dto.FriendContainerRemark;
import com.socket.socketjava.domain.dto.FriendIsContainerUser;
import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.Users.LoginVo;
import com.socket.socketjava.domain.vo.Users.RegisterVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.service.IUsersService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@RestController
@RequestMapping("/users")
@Tag(name = "用户相关接口")
@Slf4j
public class UsersController {

    @Autowired
    private IUsersService usersService;
    @Autowired
    private IFriendsService iFriendsService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        log.info("用户登录");
        String jwt = usersService.login(loginVo);
        return Result.ok(jwt);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterVo registerVo) {
        String number = usersService.register(registerVo);
        return Result.ok(number);
    }


    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<Users> getUserInfo() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        return Result.ok(usersService.getById(userId));
    }

    @Operation(summary = "根据number获取用户信息")
    @GetMapping("/infoByNumber")
    public Result<FriendIsContainerUser> getStatus(@RequestParam String number) {
        FriendIsContainerUser friendIsContainerUser = new FriendIsContainerUser();
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getNumber, number);
        Users users = usersService.getOne(usersLambdaQueryWrapper);
        friendIsContainerUser.setUserName(users.getUsername());
        friendIsContainerUser.setAvatarUrl(users.getAvatarUrl());
        friendIsContainerUser.setNumber(users.getNumber());
        friendIsContainerUser.setUserId(users.getUserId());
        // 要判断这个人是不是自己，还要判断这个人是不是好友
        // 是不是自己
        Integer userId = users.getUserId();
        Integer userId1 = UserHolder.getLoginHolder().getUserId();
        log.info("userId:" + userId + "userId1:" + userId1);
        if (Objects.equals(userId, userId1)) {
            friendIsContainerUser.setIsUser(1);
        } else {
            friendIsContainerUser.setIsUser(0);
        }

        // userId是我自己的Id,另一个是friendId
        Integer userIsFriend = iFriendsService.userIsFriend(userId1, friendIsContainerUser.getUserId());
        log.info("userIsFriend:" + userIsFriend);
        if (userIsFriend == 1) {
            friendIsContainerUser.setIsContainer(1);
        } else {
            friendIsContainerUser.setIsContainer(0);
        }
        // 满足以上则代表，既不是好友，也不是自己
        return Result.ok(friendIsContainerUser);
    }

    @Operation(summary = "根据Id查询用户信息")
    @GetMapping("/infoById")
    public Result<FriendContainerRemark> getUserInfoById(@RequestParam Integer userId) {
        Integer userId1 = UserHolder.getLoginHolder().getUserId();

        Users users = usersService.getById(userId);

        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getFriendId, userId)
                .eq(Friends::getUserId, userId1);
        String remark = iFriendsService.getOne(friendsLambdaQueryWrapper).getRemark();


        FriendContainerRemark friendContainerRemark = new FriendContainerRemark();
        friendContainerRemark.setRemark(remark);
        friendContainerRemark.setUsers(users);

        return Result.ok(friendContainerRemark);
    }


    @Operation(summary = "修改用户信息")
    @PostMapping("/update")
    public Result changeStatus(@RequestBody Users users) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        LambdaQueryWrapper<Users> usersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        usersLambdaQueryWrapper.eq(Users::getUserId, userId);
        usersService.update(users, usersLambdaQueryWrapper);
        return Result.ok();
    }

    @Operation(summary = "给别人点赞")
    @PutMapping("/like")
    public Result like(@RequestParam Integer friendId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        usersService.like(friendId, userId);
        return Result.ok();
    }

}
