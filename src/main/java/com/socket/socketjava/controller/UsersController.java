package com.socket.socketjava.controller;


import com.socket.socketjava.domain.vo.LoginVo;
import com.socket.socketjava.domain.vo.RegisterVo;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IUsersService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result logout(){
        String number = UserHolder.getLoginVo().getNumber();
        usersService.changeStatus(0,number);
        return Result.ok();
    }

    @Operation(summary = "获取用户状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        String number = UserHolder.getLoginVo().getNumber();
        Integer status = usersService.getStatusByNumber(number);
        return Result.ok(status);
    }

    @Operation(summary = "修改用户状态")
    @PostMapping("/status")
    public Result changeStatus(@RequestBody Integer status){
        String number = UserHolder.getLoginVo().getNumber();
        usersService.changeStatus(status,number);
        return Result.ok();
    }

}
