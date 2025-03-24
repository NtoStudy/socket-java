package com.socket.socketjava.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 用户动态表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/user-posts")
@Tag(name = "用户动态管理")
public class UserPostsController {

}
