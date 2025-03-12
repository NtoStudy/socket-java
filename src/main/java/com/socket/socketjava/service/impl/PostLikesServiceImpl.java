package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.PostLikes;
import com.socket.socketjava.mapper.PostLikesMapper;
import com.socket.socketjava.service.IPostLikesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 动态点赞表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@Service
public class PostLikesServiceImpl extends ServiceImpl<PostLikesMapper, PostLikes> implements IPostLikesService {

}
