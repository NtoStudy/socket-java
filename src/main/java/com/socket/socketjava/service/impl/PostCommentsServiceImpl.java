package com.socket.socketjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.pojo.PostComments;
import com.socket.socketjava.domain.pojo.UserPosts;
import com.socket.socketjava.domain.pojo.Users;
import com.socket.socketjava.mapper.PostCommentsMapper;
import com.socket.socketjava.service.IPostCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.socket.socketjava.service.IUserPostsService;
import com.socket.socketjava.service.IUsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private PostCommentsMapper postCommentsMapper;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private IUserPostsService userPostsService;

    @Override
    public void commentPost(Integer postId, Integer userId, String content, Integer parentCommentId) {
        if (parentCommentId == null) {
            // 证明此时这个评论就是父评论
            PostComments postComments = new PostComments()
                    .setPostId(postId)
                    .setUserId(userId)
                    .setContent(content)
                    .setIsDeleted(0);
            save(postComments);
        } else {
            //证明此时这个评论是子评论
            PostComments postComments = new PostComments()
                    .setPostId(postId)
                    .setUserId(userId)
                    .setContent(content)
                    .setIsDeleted(0)
                    .setParentCommentId(parentCommentId);
            save(postComments);
        }
        // 先查询这条帖子有多少评论数
        Integer commentCount = userPostsService.getById(postId).getCommentCount();
        // 无论是不是父级评论。都会增加评论数
        LambdaUpdateWrapper<UserPosts> userPostsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userPostsLambdaUpdateWrapper.eq(UserPosts::getPostId, postId)
                .eq(UserPosts::getIsDeleted, 0)
                .set(UserPosts::getCommentCount, commentCount + 1);
        userPostsService.update(userPostsLambdaUpdateWrapper);
    }


    @Override
    public List<CommentDetail> getCommentDetail(Integer postId) {
        LambdaQueryWrapper<PostComments> postCommentsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postCommentsLambdaQueryWrapper.eq(PostComments::getPostId, postId)
                .eq(PostComments::getIsDeleted, 0);
        // 当前帖子下面所有的评论
        List<CommentDetail> commentDetailList = new ArrayList<>();
        List<PostComments> postComments = postCommentsMapper.selectList(postCommentsLambdaQueryWrapper);
        if (postComments != null && !postComments.isEmpty()) {
            for (PostComments postComment : postComments) {
                CommentDetail commentDetail = new CommentDetail();
                BeanUtils.copyProperties(postComment, commentDetail);
                Integer userId = postComment.getUserId();
                Users users = usersService.getById(userId);
                commentDetail.setAvatarUrl(users.getAvatarUrl());
                commentDetail.setUsername(users.getUsername());
                // 此时是构造好了一个commentDetail
                commentDetailList.add(commentDetail);

            }
            return commentDetailList;
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteById(Integer postId, Integer commentId, Integer userId) {
        LambdaUpdateWrapper<PostComments> postCommentsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        postCommentsLambdaUpdateWrapper.eq(PostComments::getCommentId, commentId)
                .eq(PostComments::getUserId, userId)
                .set(PostComments::getIsDeleted, 1);
        update(postCommentsLambdaUpdateWrapper);
        LambdaUpdateWrapper<UserPosts> userPostsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        Integer commentCount = userPostsService.getById(postId).getCommentCount();

        userPostsLambdaUpdateWrapper.eq(UserPosts::getPostId, postId)
                .eq(UserPosts::getIsDeleted, 0)
                .set(UserPosts::getCommentCount, commentCount - 1);
        userPostsService.update(userPostsLambdaUpdateWrapper);
    }
}
