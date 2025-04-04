package com.socket.socketjava.service.impl;

import com.socket.socketjava.domain.pojo.PostComments;
import com.socket.socketjava.mapper.PostCommentsMapper;
import com.socket.socketjava.service.IPostCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 动态评论表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments> implements IPostCommentsService {

    @Override
    public void commentPost(Integer postId, Integer userId, String content, Integer parentCommentId) {

    }
}
