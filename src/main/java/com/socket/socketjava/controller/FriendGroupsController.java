package com.socket.socketjava.controller;


import com.socket.socketjava.domain.pojo.FriendGroups;
import com.socket.socketjava.domain.pojo.Friends;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IFriendGroupsService;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.service.impl.FriendGroupsServiceImpl;
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
@Tag(name = "好有分组相关接口")

public class FriendGroupsController {

    @Autowired
    private IFriendGroupsService iFriendGroupsService;
    @Autowired
    private IFriendsService iFriendsService;

    @Operation(summary = "创建好友分组")
    @PostMapping("/create")
    public Result create(String groupName) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        FriendGroups friendGroups = new FriendGroups();
        friendGroups.setUserId(userId)
                .setGroupName(groupName);
        iFriendGroupsService.save(friendGroups);
        return Result.ok();
    }

    @Operation(summary = "修改分组名称")
    @PostMapping("/update")
    public Result update(Integer groupId, String groupName) {
        FriendGroups friendGroups = new FriendGroups();
        friendGroups.setGroupId(groupId)
                .setGroupName(groupName);
        iFriendGroupsService.updateById(friendGroups);
        return Result.ok();
    }

    @Operation(summary = "删除分组")
    @DeleteMapping("/delete")
    public Result delete(Integer groupId) {
        iFriendGroupsService.removeById(groupId);
        return Result.ok();
    }

    @Operation(summary = "获取分组列表")
    @GetMapping("/list")
    public Result list() {
        Integer userId = UserHolder.getLoginHolder().getUserId();

        return Result.ok(iFriendGroupsService.lambdaQuery()
                .eq(FriendGroups::getUserId, userId)
                .list());
    }

    @Operation(summary = "查询分组内所有好友")
    @GetMapping("/friendList")
    public Result friendList(Integer groupId) {
        return Result.ok(iFriendsService.lambdaQuery()
                .eq(Friends::getGroupId, groupId)
                .list());
    }

}
