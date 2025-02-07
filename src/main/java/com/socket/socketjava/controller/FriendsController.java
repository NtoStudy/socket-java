package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.domain.vo.AcceptFriendVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.service.IUsersService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 好友关系表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@RestController
@RequestMapping("/friends")
@Tag(name = "好友相关接口")
public class FriendsController {

    @Autowired
    private IFriendsService ifriendsService;
    @Autowired
    private IUsersService iUsersService;

    @PostMapping("/add")
    @Operation(summary = "添加好友")
    public Result addFriend(@RequestParam String friendNumber) {
        String userNumber = UserHolder.getLoginHolder().getNumber();
        // 根据用户的number查询到用户的id，将用户的Id写入notifications表中
        ifriendsService.addFriend(userNumber, friendNumber);
        return Result.ok("消息已发出");
    }


//    @PutMapping("/accept")
//    @Operation(summary = "接受好友请求")
//    public Result acceptFriend(@RequestBody ) {
//
//        return Result.ok("申请以处理，请求通过");
//    }

}
