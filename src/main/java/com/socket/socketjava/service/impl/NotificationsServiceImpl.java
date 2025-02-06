package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.Notifications;
import com.socket.socketjava.mapper.NotificationsMapper;
import com.socket.socketjava.service.INotificationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统通知表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
@Service
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications> implements INotificationsService {

}
