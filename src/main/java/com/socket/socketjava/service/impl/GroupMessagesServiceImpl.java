package com.socket.socketjava.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.socket.socketjava.domain.dto.MessageListDTO;
import com.socket.socketjava.domain.pojo.ChatRooms;
import com.socket.socketjava.domain.pojo.GroupMessages;
import com.socket.socketjava.mapper.GroupMessagesMapper;
import com.socket.socketjava.service.IGroupMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 群聊消息表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-09
 */
@Service
public class GroupMessagesServiceImpl extends ServiceImpl<GroupMessagesMapper, GroupMessages> implements IGroupMessagesService {

    @Autowired
    private GroupMessagesMapper groupMessagesMapper;

    @Override
    public MessageListDTO<GroupMessages> getHistoryList(Integer userId, Integer chatRoomId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GroupMessages> groupMessagesList = groupMessagesMapper.getHistoryList(userId, chatRoomId);
        PageInfo<GroupMessages> groupMessagesPageInfo = new PageInfo<>(groupMessagesList);
        MessageListDTO<GroupMessages> groupMessagesMessageListDTO = new MessageListDTO<>();
        groupMessagesMessageListDTO.setTotal(groupMessagesPageInfo.getTotal());
        groupMessagesMessageListDTO.setList(groupMessagesList);
        groupMessagesMessageListDTO.setPageNum(groupMessagesPageInfo.getPageNum());
        groupMessagesMessageListDTO.setPageSize(groupMessagesPageInfo.getPageSize());
        groupMessagesMessageListDTO.setStartRow(groupMessagesPageInfo.getStartRow());
        groupMessagesMessageListDTO.setEndRow(groupMessagesPageInfo.getEndRow());
        groupMessagesMessageListDTO.setPages(groupMessagesPageInfo.getPages());
        return groupMessagesMessageListDTO;
    }

    @Override
    public void removeBySenderId(Integer chatRoomId, Integer messageId, Integer userId) {
         // 在这条messageId对应的消息中的deleted_by_users字段中添加userId
        groupMessagesMapper.updateByMessageId(chatRoomId, messageId, userId);
    }
}
