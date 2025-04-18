package com.socket.socketjava.service;

import com.socket.socketjava.domain.dto.GroupIsContainerUser;
import com.socket.socketjava.domain.pojo.UserChatRooms;
import com.baomidou.mybatisplus.extension.service.IService;
import com.socket.socketjava.domain.vo.Chatroom.ChatRoomListVo;
import com.socket.socketjava.domain.vo.Chatroom.CreateRoomVo;
import com.socket.socketjava.domain.vo.Chatroom.GroupCountVo;
import com.socket.socketjava.domain.vo.Notifications.AcceptRoomsVo;

import java.util.List;

/**
 * <p>
 * 用户-聊天室关联表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface IUserChatRoomsService extends IService<UserChatRooms> {

    String createChatRoom(Integer userId, CreateRoomVo createRoomVo);

    void acceptOrRejectChatRoom(Integer userId, Integer roomId, Integer status);

    List<ChatRoomListVo> getRoomList(Integer userId);

    List<Integer> getRoomUsers(Integer roomId);

    Integer getMessageCount(Integer userId, Integer roomId);

    void addGroup(Integer userId, String groupNumber);

    GroupIsContainerUser inquireGroup(String groupNumber, Integer userId);

    GroupCountVo getPinnedGroups(Integer userId);

    void setPinnedGroup(Integer userId, Integer roomId, Integer status);

    GroupCountVo getCreatedGroups(Integer userId);

    GroupCountVo getManagedGroups(Integer userId);

    boolean updateNickname(Integer userId, Integer roomId, String nickname);

    GroupCountVo getJoinedGroups(Integer userId);

    void inviteToGroup(List<Integer> friendIds, Integer roomId, Integer userId);

    String quitOrDismissGroup(Integer userId, Integer roomId);

    void changeGroupName(Integer roomId, String groupName);

    void kickOut(Integer roomId, List<Integer> userIds);

    void setAdmin(Integer roomId, Integer userId, Integer status);

    void transferOwner(Integer roomId, Integer userId, Integer userId1);

    List<AcceptRoomsVo> getGroupApplyList(Integer userId);

    void approveGroupApplication(Integer adminId, Integer userId, Integer roomId, Integer status);

    void muteUser(Integer roomId, Integer userId, Integer silenceId, Integer duration);

    void unmuteUser(Integer roomId, Integer silenceId, Integer userId);

    Object getMuteStatus(Integer roomId, Integer silenceId);
}
