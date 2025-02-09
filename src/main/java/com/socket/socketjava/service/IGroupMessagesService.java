package com.socket.socketjava.service;

import com.socket.socketjava.domain.dto.MessageListDTO;
import com.socket.socketjava.domain.pojo.GroupMessages;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 群聊消息表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-09
 */
public interface IGroupMessagesService extends IService<GroupMessages> {

    MessageListDTO<GroupMessages> getHistoryList(Integer userId, Integer chatRoomId, Integer pageNum, Integer pageSize);

    void removeBySenderId(Integer chatRoomId, Integer messageId, Integer userId);
}
