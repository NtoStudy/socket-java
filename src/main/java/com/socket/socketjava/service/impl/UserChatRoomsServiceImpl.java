package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.socket.socketjava.domain.dto.GroupIsContainerUser;
import com.socket.socketjava.domain.pojo.*;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
import com.socket.socketjava.domain.vo.Chatroom.GroupCountVo;
import com.socket.socketjava.mapper.ChatRoomsMapper;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.mapper.UserChatRoomsMapper;
import com.socket.socketjava.mapper.UsersMapper;
import com.socket.socketjava.service.IUserChatRoomsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                            .setContent(username + "聊邀请你加入群" + createRoomVo.getRoomName())
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
        // 从user表中查到userName
        Users users = usersMapper.selectById(userId);
        String username = users.getUsername();
        // 从chatRooms表中查到roomId
        LambdaQueryWrapper<ChatRooms> chatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatRoomsLambdaQueryWrapper.eq(ChatRooms::getGroupNumber, groupNumber);
        ChatRooms chatRoom = chatRoomsMapper.selectOne(chatRoomsLambdaQueryWrapper);
        Integer roomId = chatRoom.getRoomId();
        // 先保存到userChatRooms表中
        UserChatRooms userChatRooms = new UserChatRooms();
        userChatRooms.setRoomId(roomId)
                .setUserId(userId)
                .setStatus(0);
        this.save(userChatRooms);

        // 保存到系统通知表中，因为要通知对方
        notificationsMapper.insert(new Notifications()
                .setReceiverId(chatRoom.getCreatorId())
                .setRelatedId(chatRoom.getRoomId())
                .setContent(username + "申请加入群聊" + chatRoom.getRoomName())
                .setType("chatroom")
                .setStatus(0));
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
    public boolean setPinnedGroup(Integer userId, Integer roomId, Integer status) {
        LambdaUpdateWrapper<UserChatRooms> userChatRoomsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userChatRoomsLambdaUpdateWrapper.eq(UserChatRooms::getUserId, userId)
                .eq(UserChatRooms::getRoomId, roomId)
                .eq(UserChatRooms::getStatus, 1)
                .set(UserChatRooms::getIsPinned, status);

        return update(userChatRoomsLambdaUpdateWrapper);
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
    public void inviteToGroup(List<Integer> friendIds, Integer roomId) {
        ChatRooms chatRoom = chatRoomsMapper.selectById(roomId);

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
                    .setContent("你被邀请加入群聊" + chatRoom.getRoomName())
                    .setType("chatroom")
                    .setStatus(0);
            notificationsMapper.insert(notifications);
        }
    }

    @Override
    public String quitOrDismissGroup(Integer userId, Integer roomId) {
        LambdaQueryWrapper<UserChatRooms> userChatRoomsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userChatRoomsLambdaQueryWrapper.eq(UserChatRooms::getUserId, userId)
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