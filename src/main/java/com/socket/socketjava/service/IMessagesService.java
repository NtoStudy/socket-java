package com.socket.socketjava.service;

import com.socket.socketjava.domain.dto.MessageListDTO;
import com.socket.socketjava.domain.pojo.Messages;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 聊天消息表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface IMessagesService extends IService<Messages> {

    MessageListDTO<Messages> getHistoryList(Integer userId, Integer receiverId, Integer pageNum, Integer pageSize);

    void removeByMessageId(Integer messageId, Integer userId);
}
