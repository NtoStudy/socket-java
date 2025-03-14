package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.socket.socketjava.domain.dto.MessageListDTO;
import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.mapper.MessagesMapper;
import com.socket.socketjava.service.IMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 聊天消息表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, Messages> implements IMessagesService {

    @Autowired
    private MessagesMapper messagesMapper;


 

    @Cacheable(value = "privateChatHistory",key = "'private:'+#userId+':' + #receiverId+':' + #pageNum+ ':'+#pageSize")
    @Override
    public MessageListDTO<Messages> getHistoryList(Integer userId, Integer receiverId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);


        List<Messages> messagesList = messagesMapper.getHistoryList(userId, receiverId);

        // 将未读消息设置为已读
        for (Messages messages : messagesList) {
            if (messages.getIsRead() == 0 && Objects.equals(messages.getReceiverId(), userId)) {
                Integer messageId = messages.getMessageId();
                LambdaUpdateWrapper<Messages> messagesLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                messagesLambdaUpdateWrapper
                        .eq(Messages::getMessageId, messageId)
                        .eq(Messages::getReceiverId, userId)
                        .set(Messages::getIsRead, 1);
                update(messagesLambdaUpdateWrapper);
                // 更新当前列表中的消息状态
                messages.setIsRead(1);
            }
        }

        // 构造返回结果
        PageInfo<Messages> pageInfo = new PageInfo<>(messagesList);
        MessageListDTO<Messages> messagesMessageListDTO = new MessageListDTO<>();
        messagesMessageListDTO.setTotal(pageInfo.getTotal());
        messagesMessageListDTO.setList(messagesList);
        messagesMessageListDTO.setPageNum(pageInfo.getPageNum());
        messagesMessageListDTO.setPageSize(pageInfo.getPageSize());
        messagesMessageListDTO.setStartRow(pageInfo.getStartRow());
        messagesMessageListDTO.setEndRow(pageInfo.getEndRow());
        messagesMessageListDTO.setPages(pageInfo.getPages());


        return messagesMessageListDTO;
    }


    @CacheEvict(value = "privateChatHistory", allEntries = true)
    @Override
    public void removeByMessageId(Integer messageId, Integer userId) {
        // 进行判断这条消息是谁发送的，需要拿到当前的一整个messages消息
        Messages messages = getById(messageId);
        // senderId是指发送者的id
        Integer senderId = messages.getSenderId();
        if (Objects.equals(senderId, userId)) {
            // 如果是我发送的则将deleted_by_sender字段设置为1
            LambdaUpdateWrapper<Messages> messagesLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            messagesLambdaUpdateWrapper
                    .eq(Messages::getMessageId, messageId)
                    .eq(Messages::getSenderId, userId)
                    .eq(Messages::getReceiverId, messages.getReceiverId())
                    .set(Messages::getDeletedBySender, 1);
            update(messagesLambdaUpdateWrapper);
        } else if (Objects.equals(messages.getReceiverId(), userId)) {
            // 说明这条消息是对面发送的，则将deleted_by_receiver字段设置为1
            LambdaUpdateWrapper<Messages> messagesLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            messagesLambdaUpdateWrapper
                    .eq(Messages::getMessageId, messageId)
                    .eq(Messages::getSenderId, messages.getSenderId())
                    .eq(Messages::getReceiverId, userId)
                    .set(Messages::getDeletedByReceiver, 1);
            update(messagesLambdaUpdateWrapper);
        }

    }
}
