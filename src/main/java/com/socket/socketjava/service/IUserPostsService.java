package com.socket.socketjava.service;

import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.dto.LikeDetail;
import com.socket.socketjava.domain.dto.PageList;
import com.socket.socketjava.domain.dto.PostDetail;
import com.socket.socketjava.domain.pojo.UserPosts;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户动态表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
public interface IUserPostsService extends IService<UserPosts> {

    void createPost(Integer userId, String content, String mediaType, String mediaUrl);

    PostDetail getPostDetail(Integer postId);

    void topPost(Integer userId, Integer postId, Integer isTop);

    PageList<PostDetail> getPostList(Integer userId, Integer pageNum, Integer pageSize);

    PageList<PostDetail> getPostAllList(Integer userId, Integer pageNum, Integer pageSize);

    void addLikeCount(Integer postId, Integer isCancel);


//    PageList<PostDetail> getPostList(Integer userId, Integer pageNum, Integer pageSize);
}
