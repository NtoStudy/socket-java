package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.GroupMessages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 群聊消息表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-09
 */
public interface GroupMessagesMapper extends BaseMapper<GroupMessages> {

    List<GroupMessages> getHistoryList(Integer userId, Integer chatRoomId);

    void updateByMessageId(Integer chatRoomId, Integer messageId, Integer userId);
}
