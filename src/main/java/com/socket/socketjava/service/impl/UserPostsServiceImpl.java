package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.dto.LikeDetail;
import com.socket.socketjava.domain.dto.PostDetail;
import com.socket.socketjava.domain.pojo.*;
import com.socket.socketjava.mapper.PostCommentsMapper;
import com.socket.socketjava.mapper.PostLikesMapper;
import com.socket.socketjava.mapper.PostMediaMapper;
import com.socket.socketjava.mapper.UserPostsMapper;
import com.socket.socketjava.service.IUserPostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.socket.socketjava.service.IUsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户动态表 服务实现类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
@Service
public class UserPostsServiceImpl extends ServiceImpl<UserPostsMapper, UserPosts> implements IUserPostsService {

    @Autowired
    private PostMediaMapper postMediaMapper;
    @Autowired
    private UserPostsMapper userPostsMapper;
    @Autowired
    private PostCommentsMapper postCommentsMapper;
    @Autowired
    private PostLikesMapper postLikesMapper;
    @Autowired
    private IUsersService usersService;

    @Override
    public void createPost(Integer userId, String content, String mediaType, String mediaUrl) {

        UserPosts userPosts = new UserPosts();
        userPosts.setUserId(userId);
        userPosts.setContent(content);
        userPosts.setLikeCount(0);
        userPosts.setCommentCount(0);
        userPostsMapper.insert(userPosts);
        Integer postId = userPosts.getPostId();

        if (!Objects.equals(mediaUrl, "") && !Objects.equals(mediaType, "")) {
            // 有url，存到post_media
            PostMedia postMedia = new PostMedia();
            postMedia.setMediaType(mediaType)
                    .setMediaUrl(mediaUrl)
                    .setPostId(postId);
            postMediaMapper.insert(postMedia);
        }
    }

    @Override
    public PostDetail getPostDetail(Integer postId) {
        PostDetail postDetail = new PostDetail();
        UserPosts userPosts = userPostsMapper.selectById(postId);
        BeanUtils.copyProperties(userPosts, postDetail);
        PostMedia postMedia = postMediaMapper.selectById(postId);
        if (postMedia != null) {
            BeanUtils.copyProperties(postMedia, postDetail);
        }
        return postDetail;
    }

    @Override
    public List<CommentDetail> getCommentDetail(Integer postId) {
        LambdaQueryWrapper<PostComments> postCommentsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postCommentsLambdaQueryWrapper.eq(PostComments::getPostId, postId)
                .eq(PostComments::getIsDeleted, 0);
        // 当前帖子下面所有的评论
        List<PostComments> postComments = postCommentsMapper.selectList(postCommentsLambdaQueryWrapper);
        if (postComments != null) {
            for (PostComments postComment : postComments) {
                CommentDetail commentDetail = new CommentDetail();
                BeanUtils.copyProperties(postComment, commentDetail);
                Integer userId = postComment.getUserId();
                Users users = usersService.getById(userId);
                commentDetail.setAvatarUrl(users.getAvatarUrl());
                commentDetail.setUsername(users.getUsername());
                return List.of(commentDetail);
            }
        }
        return List.of();
    }

    @Override
    public List<LikeDetail> getLikeDetail(Integer postId) {
        LambdaQueryWrapper<PostLikes> postCommentsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postCommentsLambdaQueryWrapper.eq(PostLikes::getPostId, postId)
                .eq(PostLikes::getIsCancel, 0);
        // 当前帖子下的所有点赞
        List<PostLikes> postLikesList = postLikesMapper.selectList(postCommentsLambdaQueryWrapper);

        for (PostLikes postLikes : postLikesList) {
            LikeDetail likeDetail = new LikeDetail();
            BeanUtils.copyProperties(postLikes, likeDetail);
            Integer userId = postLikes.getUserId();
            Users users = usersService.getById(userId);
            likeDetail.setAvatarUrl(users.getAvatarUrl());
            likeDetail.setUsername(users.getUsername());
            return List.of(likeDetail);
        }

        return List.of();
    }
}
