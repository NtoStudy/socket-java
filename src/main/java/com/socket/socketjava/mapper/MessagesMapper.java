package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.Messages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 聊天消息表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface MessagesMapper extends BaseMapper<Messages> {

    List<Messages> getHistoryList(Integer userId, Integer receiverId);
}
