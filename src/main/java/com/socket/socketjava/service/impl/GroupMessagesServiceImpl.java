package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.GroupMessages;
import com.socket.socketjava.mapper.GroupMessagesMapper;
import com.socket.socketjava.service.IGroupMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
