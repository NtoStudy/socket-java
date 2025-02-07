package com.socket.socketjava.interceptor;

import com.socket.socketjava.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求中的JWT
        String token = jwtUtil.getTokenFromRequest(request);

        // 如果没有JWT或JWT无效，返回401 Unauthorized
        if (token == null || !isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 如果JWT有效，继续处理请求
        return true;
    }

    // 验证JWT是否有效
    private boolean isValidToken(String token) {
        try {
            jwtUtil.parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
