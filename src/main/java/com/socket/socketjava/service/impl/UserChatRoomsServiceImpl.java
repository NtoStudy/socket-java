package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectOne;
import com.socket.socketjava.domain.dto.GroupIsContainerUser;
import com.socket.socketjava.domain.pojo.*;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
import com.socket.socketjava.domain.vo.Chatroom.GroupCountVo;
import com.socket.socketjava.domain.vo.Notifications.AcceptRoomsVo;
import com.socket.socketjava.mapper.ChatRoomsMapper;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.mapper.UserChatRoomsMapper;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 用户-聊天室关联表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
@Slf4j
public class UserChatRoomsServiceImpl extends ServiceImpl<UserChatRoomsMapper, UserChatRooms> implements IUserChatRoomsService {

    @Autowired
    private UserChatRoomsMapper userChatRoomsMapper;
    @Autowired
    private ChatRoomsMapper chatRoomsMapper;
    @Autowired
    private NotificationsMapper notificationsMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private GroupMessagesServiceImpl groupMessagesService;


    @Override
    public String createChatRoom(Integer userId, CreateRoomVo createRoomVo) {
        // 新建一个聊天室 插入到聊天室表
        ChatRooms chatRooms = new ChatRooms();
        chatRooms.setRoomName(createRoomVo.getRoomName());
        chatRooms.setCreatorId(userId);
        String groupNumber = generateUniqueNumber();
        chatRooms.setGroupNumber(groupNumber);
        chatRoomsMapper.insert(chatRooms);
        // 通过用户的id获取用户的username
        Integer roomId = chatRooms.getRoomId();
        String username = usersMapper.selectById(userId).getUsername();
        // 先将用户自己插入到聊天室内
        UserChatRooms userChatRooms1 = new UserChatRooms();
        userChatRooms1.setRole("群主");
        userChatRooms1.setUserId(userId);
        userChatRooms1.setRoomId(roomId);
        userChatRooms1.setStatus(1);
        userChatRoomsMapper.insert(userChatRooms1);
        // 拿到聊天室的id 插入用户聊天室表中
        for (Integer userIds : createRoomVo.getUserIds()) {
            UserChatRooms userChatRooms = new UserChatRooms();
            userChatRooms.setUserId(userIds);
            userChatRooms.setRoomId(roomId);
            userChatRooms.setRole("普通成员");
            userChatRooms.setStatus(0);
            userChatRoomsMapper.insert(userChatRooms);
            // 要将邀请信息插入到系统通知表中，以便用户得知信息
            notificationsMapper.insert(
                    new Notifications()
                            .setReceiverId(userIds)
                            .setRelatedId(roomId)
                            .setContent(username + "邀请你加入群" + createRoomVo.getRoomName())
                            .setType("chatroom")
                            .setStatus(0)
            );
        }
        return groupNumber;
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


    @Override
    public void acceptOrRejectChatRoom(Integer userId, Integer roomId, Integer status) {
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper
                .eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .set(UserChatRooms::getStatus, status);
        if (status == 1) {
            // 如果接受邀请，设置为普通成员
            userChatRoomsLambdaUpdateWrapper.set(UserChatRooms::getRole, "普通成员");
        }

        // 查询群聊信息
        ChatRooms chatRoom = chatRoomsMapper.selectById(roomId);

        // 查询邀请通知以获取邀请人ID
        LambdaQueryWrapper<Notifications> notificationQuery = new LambdaQueryWrapper<>();
        notificationQuery.eq(Notifications::getReceiverId, userId)
                .eq(Notifications::getRelatedId, roomId)
                .eq(Notifications::getType, "chatroom");
        Notifications invitation = notificationsMapper.selectOne(notificationQuery);

        Integer inviterId = invitation != null && invitation.getCreatorId() != null ?
                invitation.getCreatorId() : chatRoom.getCreatorId();

        // 发送通知给邀请人
        if (inviterId != null) {
            Users user = usersMapper.selectById(userId);
            String username = user != null ? user.getUsername() : "用户";

            Notifications notification = new Notifications();
            notification.setReceiverId(inviterId)
                    .setRelatedId(roomId)
                    .setContent(username + (status == 1 ? "已接受您的群聊邀请" : "已拒绝您的群聊邀请"))
                    .setType("group_invitation_response")
                    .setStatus(0)
                    .setCreatorId(userId);  // 设置响应者ID为creator_id
            notificationsMapper.insert(notification);
        }


        update(userChatRoomsLambdaUpdateWrapper);
    }

    @Override
    public List<ChatRoomListVo> getRoomList(Integer userId) {
        return userChatRoomsMapper.selectRoomList(userId);
    }

    @Override
    public List<Integer> getRoomUsers(Integer roomId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1);
        List<UserChatRooms> userChatRooms = userChatRoomsMapper.selectList(userChatRoomsLambdaQueryWrapper);
        return userChatRooms.stream().map(UserChatRooms::getUserId).toList();
    }

    @Override
    public Integer getMessageCount(Integer userId, Integer roomId) {
        // 根据roomId来查询
        LambdaQueryWrapper<GroupMessages> groupMessagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询未读的消息需要的条件，chatroom，sender_id
        groupMessagesLambdaQueryWrapper
                .eq(GroupMessages::getChatRoomId, roomId)
                .ne(GroupMessages::getSenderId, userId)
                .eq(GroupMessages::getIsRead, 0);
        Integer count = Math.toIntExact(groupMessagesService.count(groupMessagesLambdaQueryWrapper));
        if (count <= 99) return count;
        else return 100;
    }

    @Override
    public void addGroup(Integer userId, String groupNumber) {
        // 查询群聊信息
        LambdaQueryWrapper<ChatRooms> chatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatRoomsLambdaQueryWrapper.eq(ChatRooms::getGroupNumber, groupNumber);
        ChatRooms chatRooms = chatRoomsMapper.selectOne(chatRoomsLambdaQueryWrapper);
        if (chatRooms == null) {
            throw new RuntimeException("群聊不存在");
        }

        // 获取用户名
        String username = usersMapper.selectById(userId).getUsername();

        // 查询群主和管理员
        LambdaQueryWrapper<UserChatRooms> adminQueryWrapper = new LambdaQueryWrapper<>();
        adminQueryWrapper
                .eq(UserChatRooms::getRoomId, chatRooms.getRoomId())
                .eq(UserChatRooms::getStatus, 1)
                .in(UserChatRooms::getRole, "群主", "管理员");
        List<UserChatRooms> admins = userChatRoomsMapper.selectList(adminQueryWrapper);

        // 向群主和管理员发送通知
        for (UserChatRooms admin : admins) {
            Notifications notification = new Notifications()
                    .setReceiverId(admin.getUserId())
                    .setRelatedId(chatRooms.getRoomId())
                    .setContent(username + "申请加入群聊" + chatRooms.getRoomName())
                    .setType("group_apply")
                    .setStatus(0)
                    .setCreatorId(userId);
            notificationsMapper.insert(notification);
        }

        // 将用户添加到群聊中，状态为待审核
        UserChatRooms newUserChatRoom = new UserChatRooms();
        newUserChatRoom.setUserId(userId);
        newUserChatRoom.setRoomId(chatRooms.getRoomId());
        newUserChatRoom.setRole("普通成员");
        newUserChatRoom.setStatus(0);  // 待审核状态
        userChatRoomsMapper.insert(newUserChatRoom);
    }


    @Override
    public GroupIsContainerUser inquireGroup(String groupNumber, Integer userId) {
        GroupIsContainerUser groupIsContainerUser = new GroupIsContainerUser();
        LambdaQueryWrapper<ChatRooms> chatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatRoomsLambdaQueryWrapper.eq(ChatRooms::getGroupNumber, groupNumber);
        ChatRooms chatRoom = chatRoomsMapper.selectOne(chatRoomsLambdaQueryWrapper);

        groupIsContainerUser.setGroupNumber(chatRoom.getGroupNumber());
        groupIsContainerUser.setRoomId(chatRoom.getRoomId());
        groupIsContainerUser.setRoomName(chatRoom.getRoomName());
        groupIsContainerUser.setAvatarUrl(chatRoom.getAvatarUrl());

        // 判断用户是否在群聊中
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, groupIsContainerUser.getRoomId())
                .eq(UserChatRooms::getStatus, 1);

        if (getOne(userChatRoomsLambdaQueryWrapper) != null) {
            groupIsContainerUser.setIsContainer(1);
        } else {
            groupIsContainerUser.setIsContainer(0);
        }

        return groupIsContainerUser;
    }

    @Override
    public void setPinnedGroup(Integer userId, Integer roomId, Integer status) {
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1)
                .set(UserChatRooms::getIsPinned, status);

        update(userChatRoomsLambdaUpdateWrapper);
    }

    @Override
    public GroupCountVo getPinnedGroups(Integer userId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getStatus, 1)
                .eq(UserChatRooms::getIsPinned, 1);

        return getGroupCountVo(userChatRoomsLambdaQueryWrapper);
    }

    @Override
    public GroupCountVo getCreatedGroups(Integer userId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getStatus, 1)
                .eq(UserChatRooms::getRole, "群主");

        return getGroupCountVo(userChatRoomsLambdaQueryWrapper);
    }


    @Override
    public GroupCountVo getManagedGroups(Integer userId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getStatus, 1)
                .eq(UserChatRooms::getRole, "管理员");

        return getGroupCountVo(userChatRoomsLambdaQueryWrapper);
    }

    @Override
    public GroupCountVo getJoinedGroups(Integer userId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getStatus, 1)
                .eq(UserChatRooms::getRole, "普通成员");

        return getGroupCountVo(userChatRoomsLambdaQueryWrapper);
    }

    @Override
    public boolean updateNickname(Integer userId, Integer roomId, String nickname) {
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1)
                .set(UserChatRooms::getNickname, nickname);

        return update(userChatRoomsLambdaUpdateWrapper);
    }

    @Override
    public void inviteToGroup(List<Integer> friendIds, Integer roomId, Integer userId) {
        ChatRooms chatRoom = chatRoomsMapper.selectById(roomId);
        String username = usersMapper.selectById(userId).getUsername();
        for (Integer friendId : friendIds) {
            UserChatRooms userChatRooms = new UserChatRooms();
            userChatRooms.setUserId(friendId)
                    .setRoomId(roomId)
                    .setStatus(0)
                    .setRole("普通成员")
                    .setIsPinned(0);
            save(userChatRooms);

            // 插入到通知表中
            Notifications notifications = new Notifications();
            notifications.setReceiverId(friendId)
                    .setRelatedId(roomId)
                    .setContent(username + "邀请你加入群" + chatRoom.getRoomName())
                    .setType("chatroom")
                    .setStatus(0)
                    .setCreatorId(userId);
            notificationsMapper.insert(notifications);
        }
    }

    @Override
    public String quitOrDismissGroup(Integer userId, Integer roomId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper
                .eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1);
        UserChatRooms userChatRooms = getOne(userChatRoomsLambdaQueryWrapper);

        String role = userChatRooms.getRole();
        if (role.equals("群主")) {
            LambdaUpdateWrapper<UserChatRooms> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserChatRooms::getRoomId, roomId)
                    .set(UserChatRooms::getStatus, 0);
            update(updateWrapper);
            return "群聊已解散";
        } else if (role.equals("普通成员") || role.equals("管理员")) {
            // 普通成员，直接退出
            LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userChatRoomsLambdaUpdateWrapper.eq(UserChatRooms::getUserId, userId)
                    .eq(UserChatRooms::getRoomId, roomId)
                    .eq(UserChatRooms::getStatus, 1)
                    .set(UserChatRooms::getStatus, 0);
            update(userChatRoomsLambdaUpdateWrapper);
            return "已退出群聊";
        }

        return "操作失败";
    }

    @Override
    public void changeGroupName(Integer roomId, String groupName) {
        LambdaUpdateWrapper<ChatRooms> chatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        chatRoomsLambdaUpdateWrapper.eq(ChatRooms::getRoomId, roomId)
                .set(ChatRooms::getRoomName, groupName);
        chatRoomsMapper.update(chatRoomsLambdaUpdateWrapper);
    }

    @Override
    public void kickOut(Integer roomId, List<Integer> userIds) {
        for (Integer userId : userIds) {
            LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userChatRoomsLambdaUpdateWrapper
                    .eq(UserChatRooms::getUserId, userId)
                    .eq(UserChatRooms::getRoomId, roomId)
                    .set(UserChatRooms::getStatus, 0);
            update(userChatRoomsLambdaUpdateWrapper);
        }

    }

    @Override
    public void setAdmin(Integer roomId, Integer userId, Integer status) {
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper
                .eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1)
                .set(UserChatRooms::getRole, status == 1 ? "管理员" : "普通成员");
        update(userChatRoomsLambdaUpdateWrapper);
    }

    @Override
    public void transferOwner(Integer roomId, Integer userId, Integer userId1) {
        // 将别人变成群主
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper
                .eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1)
                .set(UserChatRooms::getRole, "群主");
        update(userChatRoomsLambdaUpdateWrapper);
        // 自己变成普通成员
        LambdaUpdateWrapper<UserChatRooms> chatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        chatRoomsLambdaUpdateWrapper
                .eq(UserChatRooms::getUserId, userId1)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1)
                .set(UserChatRooms::getRole, "普通成员");
        update(chatRoomsLambdaUpdateWrapper);
    }

    @Override
    public List<AcceptRoomsVo> getGroupApplyList(Integer userId) {
        // 获取用户作为管理员或群主的群聊申请
        List<AcceptRoomsVo> applyList = notificationsMapper.selectGroupAppliesByReceiverId(userId);

        // 将通知状态更新为已读
        LambdaUpdateWrapper<Notifications> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(Notifications::getReceiverId, userId)
                .eq(Notifications::getType, "group_apply")
                .eq(Notifications::getStatus, 0)
                .set(Notifications::getStatus, 1);
        notificationsMapper.update(null, updateWrapper);

        return applyList;
    }


    @Override
    public void approveGroupApplication(Integer adminId, Integer userId, Integer roomId, Integer status) {
        // 1. 验证审批人是否为群主或管理员
        LambdaQueryWrapper<UserChatRooms> adminQueryWrapper = new LambdaQueryWrapper<>();
        adminQueryWrapper
                .eq(UserChatRooms::getUserId, adminId)
                .eq(UserChatRooms::getRoomId, roomId)
                .in(UserChatRooms::getRole, "群主", "管理员");
        UserChatRooms admin = userChatRoomsMapper.selectOne(adminQueryWrapper);
        if (admin == null) {
            throw new RuntimeException("您没有权限审批该申请");
        }

        // 2. 查询申请人的申请记录
        LambdaQueryWrapper<UserChatRooms> applicantQueryWrapper = new LambdaQueryWrapper<>();
        applicantQueryWrapper
                .eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId);
        UserChatRooms applicant = userChatRoomsMapper.selectOne(applicantQueryWrapper);
        if (applicant == null) {
            throw new RuntimeException("未找到该用户的申请记录");
        }

        // 3. 获取群聊和用户信息
        ChatRooms chatRoom = chatRoomsMapper.selectById(roomId);
        Users user = usersMapper.selectById(userId);
        if (chatRoom == null || user == null) {
            throw new RuntimeException("群聊或用户不存在");
        }

        // 4. 处理申请
        if (status == 1) {
            // 接受申请
            LambdaUpdateWrapper<UserChatRooms> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper
                    .eq(UserChatRooms::getUserId, userId)
                    .eq(UserChatRooms::getRoomId, roomId)
                    .set(UserChatRooms::getStatus, 1); // 设置为已加入状态
            userChatRoomsMapper.update(null, updateWrapper);

            // 发送通知给申请人
            Notifications notification = new Notifications();
            notification.setReceiverId(userId)
                    .setContent("您的入群申请已被接受，欢迎加入「" + chatRoom.getRoomName() + "」")
                    .setType("group_apply_response")
                    .setStatus(0)
                    .setRelatedId(roomId)
                    .setCreatorId(adminId);  // 设置管理员ID为creator_id
            notificationsMapper.insert(notification);

        } else if (status == 2) {
            // 拒绝申请
            LambdaUpdateWrapper<UserChatRooms> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper
                    .eq(UserChatRooms::getUserId, userId)
                    .eq(UserChatRooms::getRoomId, roomId);
            userChatRoomsMapper.delete(updateWrapper); // 删除申请记录

            // 发送通知给申请人
            Notifications notification = new Notifications();
            notification.setReceiverId(userId);
            notification.setContent("您申请加入「" + chatRoom.getRoomName() + "」的请求已被拒绝");
            notification.setType("chatroom");
            notification.setStatus(0);
            notification.setRelatedId(roomId);
            notification.setCreatorId(adminId);
            notificationsMapper.insert(notification);
        } else {
            throw new RuntimeException("无效的审批状态");
        }

        // 5. 更新通知状态
        LambdaUpdateWrapper<Notifications> notificationUpdateWrapper = new LambdaUpdateWrapper<>();
        notificationUpdateWrapper
                .eq(Notifications::getType, "group_apply")
                .eq(Notifications::getRelatedId, roomId)
                .eq(Notifications::getCreatorId, userId)
                .set(Notifications::getStatus, 1); // 标记为已处理
        notificationsMapper.update(null, notificationUpdateWrapper);
    }

    @Override
    public void muteUser(Integer roomId, Integer userId, Integer silenceId, Integer duration) {
        //TODO 禁言三件套先搁置一下
    }

    @Override
    public void unmuteUser(Integer roomId, Integer silenceId, Integer userId) {

    }

    @Override
    public Object getMuteStatus(Integer roomId, Integer silenceId) {
        return null;
    }


    // 提取公共方法
    private GroupCountVo getGroupCountVo(LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper) {
        GroupCountVo groupCountVo = new GroupCountVo();
        List<UserChatRooms> userChatRoomsList = list(userChatRoomsLambdaQueryWrapper);

        int size = userChatRoomsList.size();
        groupCountVo.setGroupCount(size);

        List<ChatRooms> chatRoomsList = new ArrayList<>();
        for (UserChatRooms userChatRoom : userChatRoomsList) {
            Integer roomId = userChatRoom.getRoomId();
            ChatRooms chatRoom = chatRoomsMapper.selectById(roomId);
            if (chatRoom != null) {
                chatRoomsList.add(chatRoom);
            }
        }
        groupCountVo.setChatRoomsList(chatRoomsList);
        return groupCountVo;
    }
}