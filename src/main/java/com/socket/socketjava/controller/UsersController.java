package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.dto.FriendPlus;
import com.socket.socketjava.domain.dto.FriendIsContainerUser;
import com.socket.socketjava.domain.dto.GroupUserInfo;
import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.Users.LoginVo;
import com.socket.socketjava.domain.vo.Users.RegisterVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.socket.socketjava.service.IUsersService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
@Tag(name = "用户账号管理")
@Slf4j
public class UsersController {

    @Autowired
    private IUsersService usersService;
    @Autowired
    private IUserChatRoomsService userChatRoomsService;

    @Operation(summary = "用户账号登录验证")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        log.info("用户登录");
        String jwt = usersService.login(loginVo);
        return Result.ok(jwt);
    }

    @Operation(summary = "新用户账号注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterVo registerVo) {
        String number = usersService.register(registerVo);
        return Result.ok(number);
    }


    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<Users> getUserInfo() {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        return Result.ok(usersService.getById(userId));
    }

    @Operation(summary = "根据用户ID查询好友详细信息")
    @GetMapping("/infoById")
    public Result<FriendPlus> getUserInfoById(@RequestParam Integer userId) {
        Integer currentUserId = UserHolder.getLoginHolder().getUserId();
        FriendPlus friendPlus = usersService.getUserInfoWithFriendRelation(userId, currentUserId);
        return Result.ok(friendPlus);
    }


    @Operation(summary = "在群聊中根据用户ID查询用户详细信息")
    @GetMapping("/userInfoByIdInGroup")
    public Result<GroupUserInfo> UserInfoById(@RequestParam Integer userId, Integer roomId) {
        GroupUserInfo groupUserInfo = new GroupUserInfo();
        Users users = usersService.getById(userId);
        groupUserInfo.setAvatarUrl(users.getAvatarUrl());
        groupUserInfo.setNumber(users.getNumber());
        groupUserInfo.setUserId(users.getUserId());
        groupUserInfo.setUsername(users.getUsername());

        groupUserInfo.setStatus(users.getStatus());
        groupUserInfo.setCustomStatus(users.getCustomStatus());
        groupUserInfo.setHobbies(users.getHobbies());
        groupUserInfo.setLikeCount(users.getLikeCount());




        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1);
        groupUserInfo.setNickname(userChatRoomsService.getOne(userChatRoomsLambdaQueryWrapper).getNickname());
        groupUserInfo.setRole(userChatRoomsService.getOne(userChatRoomsLambdaQueryWrapper).getRole());
        return Result.ok(groupUserInfo);
    }

    @Operation(summary = "根据用户账号查询用户信息")
    @GetMapping("/infoByNumber")
    public Result<FriendIsContainerUser> getStatus(@RequestParam String number) {
        Integer currentUserId = UserHolder.getLoginHolder().getUserId();
        FriendIsContainerUser friendIsContainerUser = usersService.getUserInfoByNumber(number, currentUserId);
        return Result.ok(friendIsContainerUser);
    }

    @Operation(summary = "更新当前用户的个人信息")
    @PostMapping("/update")
    public Result changeStatus(@RequestBody Users users) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        usersService.updateUserInfo(users, userId);
        return Result.ok();
    }

    @Operation(summary = "给指定用户点赞")
    @PutMapping("/like")
    public Result like(@RequestParam Integer friendId) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        usersService.like(friendId, userId);
        return Result.ok();
    }

}
