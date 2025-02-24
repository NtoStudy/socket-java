package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.ChatRooms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.socket.socketjava.domain.vo.Notifications.AcceptRoomsVo;

import java.util.List;

/**
 * <p>
 * 聊天室表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface ChatRoomsMapper extends BaseMapper<ChatRooms> {


    List<AcceptRoomsVo> selectByCreatorId(Integer userId);

    List<AcceptRoomsVo> selectManager(Integer userId);
}
