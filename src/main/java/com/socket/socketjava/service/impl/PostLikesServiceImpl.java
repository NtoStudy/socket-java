package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    @Override
    public void likePost(Integer postId, Integer userId, Integer isCancel) {
        // 先查看一下有没有这个记录
        LambdaQueryWrapper<PostLikes> postLikesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLikesLambdaQueryWrapper.eq(PostLikes::getPostId, postId)
                .eq(PostLikes::getUserId, userId);
        PostLikes postLikes = getOne(postLikesLambdaQueryWrapper);

        if (postLikes != null) {
            // 证明有这个点赞记录
            LambdaUpdateWrapper<PostLikes> postLikesLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            postLikesLambdaUpdateWrapper.eq(PostLikes::getPostId, postId)
                    .eq(PostLikes::getUserId, userId)
                    .set(PostLikes::getIsCancel, isCancel);
            update(postLikesLambdaUpdateWrapper);
        }else{
            // 证明没有这个点赞尽记录
            PostLikes postLikes1 = new PostLikes();
            postLikes1.setPostId(postId);
            postLikes1.setUserId(userId);
            postLikes1.setIsCancel(isCancel);
            save(postLikes1);
        }
    }
}
