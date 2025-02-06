package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.Messages;
import com.socket.socketjava.mapper.MessagesMapper;
import com.socket.socketjava.service.IMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
