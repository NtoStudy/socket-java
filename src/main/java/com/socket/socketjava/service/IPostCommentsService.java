package com.socket.socketjava.service;

import com.socket.socketjava.domain.dto.CommentDetail;
import com.socket.socketjava.domain.pojo.PostComments;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 动态评论表 服务类
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-12
 */
public interface IPostCommentsService extends IService<PostComments> {

    void commentPost(Integer postId, Integer userId, String content, Integer parentCommentId);

    List<CommentDetail> getCommentDetail(Integer postId);

    void deleteById(Integer postId, Integer commentId, Integer userId);
}
