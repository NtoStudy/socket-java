package com.socket.socketjava.ws;

import com.socket.socketjava.utils.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 自定义握手拦截器，用于WebSocket连接握手时处理用户信息
 */
@Slf4j
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手前执行的方法，用于验证和设置用户信息
     *
     * @param request    服务器HTTP请求，包含客户端请求信息
     * @param response   服务器HTTP响应，用于向客户端发送响应
     * @param wsHandler  WebSocket处理器，处理WebSocket连接
     * @param attributes 属性集合，用于设置WebSocket会话属性
     * @return 返回布尔值，决定是否继续握手过程
     * @throws Exception 抛出异常，握手过程中可能发生的错误
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求的查询参数中获取用户ID
        String query = request.getURI().getQuery();
        //去除query的前6个字符
        String token = query.substring(6);
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = claims.get("userId", Integer.class);

        // 如果用户ID为空或无效，则终止握手
        if (userId == null) {
            return false; // JWT 校验失败
        }
        // 将用户ID添加到属性集合中，以便在WebSocket会话中使用
        attributes.put("userId", userId);
        // 继续握手过程
        return true;
    }

    /**
     * 握手后执行的方法，此处未实现任何操作
     *
     * @param request   服务器HTTP请求，包含客户端请求信息
     * @param response  服务器HTTP响应，用于向客户端发送响应
     * @param wsHandler WebSocket处理器，处理WebSocket连接
     * @param exception 异常对象，握手过程中可能发生的异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 此处可根据需要添加握手后的处理逻辑
    }
}
