package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.dto.LikeDetail;
import com.socket.socketjava.domain.dto.PageList;
import com.socket.socketjava.domain.dto.PostDetail;
import com.socket.socketjava.domain.pojo.*;
import com.socket.socketjava.mapper.PostCommentsMapper;
import com.socket.socketjava.mapper.PostLikesMapper;
import com.socket.socketjava.mapper.PostMediaMapper;
import com.socket.socketjava.mapper.UserPostsMapper;
import com.socket.socketjava.service.IFriendsService;
import com.socket.socketjava.service.IUserPostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.socket.socketjava.service.IUsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private IFriendsService friendsService;

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
    public void topPost(Integer userId, Integer postId, Integer isTop) {
        LambdaUpdateWrapper<UserPosts> userPostsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userPostsLambdaUpdateWrapper.eq(UserPosts::getPostId, postId)
                .eq(UserPosts::getUserId, userId)
                .eq(UserPosts::getIsDeleted, 0)
                .set(UserPosts::getIsPinned, isTop);
        update(userPostsLambdaUpdateWrapper);
    }

    @Override
    public PageList<PostDetail> getPostList(Integer userId, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<UserPosts> page = new Page<>(pageNum, pageSize);

        // 创建查询条件：只查询自己的动态
        LambdaQueryWrapper<UserPosts> userPostsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userPostsLambdaQueryWrapper
                .eq(UserPosts::getUserId, userId)
                .eq(UserPosts::getIsDeleted, 0)
                .orderByDesc(UserPosts::getIsDeleted)
                .orderByDesc(UserPosts::getCreatedAt);

        // 执行分页查询 这里查询到的是所有的 自己发布的 动态
        Page<UserPosts> postsPage = userPostsMapper.selectPage(page, userPostsLambdaQueryWrapper);

        // 转换为PostDetail列表
        List<PostDetail> postDetails = new ArrayList<>();
        for (UserPosts post : postsPage.getRecords()) {
            PostDetail detail = new PostDetail();
            BeanUtils.copyProperties(post, detail);

            // 获取媒体信息
            LambdaQueryWrapper<PostMedia> mediaQueryWrapper = new LambdaQueryWrapper<>();
            mediaQueryWrapper.eq(PostMedia::getPostId, post.getPostId());
            PostMedia media = postMediaMapper.selectOne(mediaQueryWrapper);
            if (media != null) {
                detail.setMediaId(media.getMediaId());
                detail.setMediaType(media.getMediaType());
                detail.setMediaUrl(media.getMediaUrl());
            }

            postDetails.add(detail);
        }

        // 构建返回结果
        PageList<PostDetail> result = new PageList<>();
        result.setTotal(postsPage.getTotal());
        result.setList(postDetails);
        result.setPageNum((int) postsPage.getCurrent());
        result.setPageSize((int) postsPage.getSize());
        result.setPages((int) postsPage.getPages());

        return result;
    }

    @Override
    public PageList<PostDetail> getPostAllList(Integer userId, Integer pageNum, Integer pageSize) {
        Page<UserPosts> page = new Page<>(pageNum, pageSize);

        // 查询用户好友
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getUserId, userId)
                .eq(Friends::getStatus, 1);
        List<Friends> list = friendsService.list(friendsLambdaQueryWrapper);

        // 获得所有好友id
        List<Integer> friendIds = list.stream().map(Friends::getFriendId).toList();
        // 添加自己的ID，这样也能看到自己的朋友圈
        List<Integer> allUserIds = new ArrayList<>(friendIds);
        allUserIds.add(userId);

        // 创建查询条件：查询所有好友和自己的动态
        LambdaQueryWrapper<UserPosts> userPostsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userPostsLambdaQueryWrapper
                .in(UserPosts::getUserId, allUserIds)  // 使用in查询所有好友和自己的动态
                .eq(UserPosts::getIsDeleted, 0)
                .orderByDesc(UserPosts::getCreatedAt); // 按时间倒序排序

        // 执行分页查询
        Page<UserPosts> postsPage = userPostsMapper.selectPage(page, userPostsLambdaQueryWrapper);

        // 转换为PostDetail列表
        List<PostDetail> postDetails = new ArrayList<>();
        for (UserPosts post : postsPage.getRecords()) {
            PostDetail detail = new PostDetail();
            BeanUtils.copyProperties(post, detail);

            // 获取媒体信息
            LambdaQueryWrapper<PostMedia> mediaQueryWrapper = new LambdaQueryWrapper<>();
            mediaQueryWrapper.eq(PostMedia::getPostId, post.getPostId());
            PostMedia media = postMediaMapper.selectOne(mediaQueryWrapper);
            if (media != null) {
                detail.setMediaType(media.getMediaType());
                detail.setMediaUrl(media.getMediaUrl());
            }

            postDetails.add(detail);
        }

        // 构建返回结果
        PageList<PostDetail> result = new PageList<>();
        result.setTotal(postsPage.getTotal());
        result.setList(postDetails);
        result.setPageNum((int) postsPage.getCurrent());
        result.setPageSize((int) postsPage.getSize());
        result.setPages((int) postsPage.getPages());

        return result;
    }

    @Override
    public void addLikeCount(Integer postId, Integer isCancel) {
        LambdaQueryWrapper<UserPosts> userPostsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userPostsLambdaQueryWrapper.eq(UserPosts::getPostId, postId)
                .eq(UserPosts::getIsDeleted, 0);
        UserPosts userPosts = userPostsMapper.selectOne(userPostsLambdaQueryWrapper);
        Integer likeCount = userPosts.getLikeCount();

        LambdaUpdateWrapper<UserPosts> userPostsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userPostsLambdaUpdateWrapper.eq(UserPosts::getPostId, postId)
                .eq(UserPosts::getIsDeleted, 0);
        if (isCancel == 0) {
            // 代表数量加一
            update(userPostsLambdaUpdateWrapper.set(UserPosts::getLikeCount, likeCount + 1));
        } else if (isCancel == 1) {
            // 代表数量减一
            update(userPostsLambdaUpdateWrapper.set(UserPosts::getLikeCount, likeCount - 1));

        }
    }


}
