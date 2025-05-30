package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.socket.socketjava.domain.dto.PageList;
import com.socket.socketjava.domain.pojo.GroupMessages;
import com.socket.socketjava.mapper.GroupMessagesMapper;
import com.socket.socketjava.service.IGroupMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "groupChatHistory", key = "'history:' + #chatRoomId + ':' + #pageNum + ':' + #pageSize")
    public PageList<GroupMessages> getHistoryList(Integer userId, Integer chatRoomId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GroupMessages> groupMessagesList = groupMessagesMapper.getHistoryList(userId, chatRoomId);

        for (GroupMessages groupMessages : groupMessagesList) {
            Integer messageId = groupMessages.getMessageId();
            LambdaUpdateWrapper<GroupMessages> groupMessagesLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            groupMessagesLambdaUpdateWrapper
                    .eq(GroupMessages::getMessageId, messageId)
                    .eq(GroupMessages::getChatRoomId, chatRoomId)
                    .set(GroupMessages::getIsRead, 1);
            update(groupMessagesLambdaUpdateWrapper);
        }


        PageInfo<GroupMessages> groupMessagesPageInfo = new PageInfo<>(groupMessagesList);
        PageList<GroupMessages> groupMessagesPageList = new PageList<>();
        groupMessagesPageList.setTotal(groupMessagesPageInfo.getTotal());
        groupMessagesPageList.setList(groupMessagesList);
        groupMessagesPageList.setPageNum(groupMessagesPageInfo.getPageNum());
        groupMessagesPageList.setPageSize(groupMessagesPageInfo.getPageSize());
        groupMessagesPageList.setStartRow(groupMessagesPageInfo.getStartRow());
        groupMessagesPageList.setEndRow(groupMessagesPageInfo.getEndRow());
        groupMessagesPageList.setPages(groupMessagesPageInfo.getPages());
        return groupMessagesPageList;
    }

    @Override
    @CacheEvict(value = "groupChatHistory", allEntries = true)
    public void removeBySenderId(Integer chatRoomId, Integer messageId, Integer userId) {
         // 在这条messageId对应的消息中的deleted_by_users字段中添加userId
        groupMessagesMapper.updateByMessageId(chatRoomId, messageId, userId);
    }
}
