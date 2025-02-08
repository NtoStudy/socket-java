package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.Notifications;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 系统通知表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface NotificationsMapper extends BaseMapper<Notifications> {


    Notifications selectByReceiverId(Integer userId);


}
