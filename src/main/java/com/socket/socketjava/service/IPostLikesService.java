package com.socket.socketjava.service;

import com.socket.socketjava.domain.pojo.PostLikes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 动态点赞表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
public interface IPostLikesService extends IService<PostLikes> {

    void likePost(Integer postId, Integer userId, Integer isCancel);
}
