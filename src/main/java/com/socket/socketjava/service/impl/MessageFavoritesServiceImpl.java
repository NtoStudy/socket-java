package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.MessageFavorites;
import com.socket.socketjava.mapper.MessageFavoritesMapper;
import com.socket.socketjava.service.IMessageFavoritesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息收藏表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@Service
public class MessageFavoritesServiceImpl extends ServiceImpl<MessageFavoritesMapper, MessageFavorites> implements IMessageFavoritesService {

}
