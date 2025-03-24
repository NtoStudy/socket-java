package com.socket.socketjava.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.socket.socketjava.domain.pojo.FriendGroups;
import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IFriendGroupsService;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 好友分组表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/friend-groups")
@Tag(name = "好友分组管理")

public class FriendGroupsController {

    @Autowired
    private IFriendGroupsService iFriendGroupsService;
    @Autowired
    private IFriendsService iFriendsService;

    @Operation(summary = "创建新的好友分组")
    @PostMapping("/create")
    public Result create(String groupName) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        FriendGroups friendGroups = new FriendGroups();
        friendGroups.setUserId(userId)
                .setGroupName(groupName);
        iFriendGroupsService.save(friendGroups);
        return Result.ok();
    }

    @Operation(summary = "修改已有分组的名称") 
    @PostMapping("/update")
    public Result update(Integer groupId, String groupName) {
        LambdaUpdateWrapper<FriendGroups> friendGroupsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        friendGroupsLambdaUpdateWrapper.eq(FriendGroups::getGroupId, groupId)
                .set(FriendGroups::getGroupName, groupName);
        iFriendGroupsService.update(friendGroupsLambdaUpdateWrapper);
        return Result.ok();
    }

    @Operation(summary = "删除指定的好友分组") 
    @DeleteMapping("/delete")
    public Result delete(Integer groupId) {
        iFriendGroupsService.removeById(groupId);
        return Result.ok();
    }

    @Operation(summary = "获取当前用户的所有分组")
    @GetMapping("/list")
    public Result list() {
        Integer userId = UserHolder.getLoginHolder().getUserId();

        return Result.ok(iFriendGroupsService.lambdaQuery()
                .eq(FriendGroups::getUserId, userId)
                .list());
    }

    @Operation(summary = "查询指定分组内的所有好友") 
    @GetMapping("/friendList")
    public Result friendList(Integer groupId) {
        return Result.ok(iFriendsService.lambdaQuery()
                .eq(Friends::getGroupId, groupId)
                .list());
    }

}
