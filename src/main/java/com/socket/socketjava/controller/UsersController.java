package com.socket.socketjava.controller;


import com.socket.socketjava.domain.vo.LoginVo;
import com.socket.socketjava.domain.vo.RegisterVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
public class UsersController {

    @Autowired
    private IUsersService usersService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginVo loginVo){
            String jwt = usersService.login(loginVo);
        return Result.ok(jwt);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterVo registerVo){

        String number = usersService.register(registerVo);

        return Result.ok(number);
    }


}
