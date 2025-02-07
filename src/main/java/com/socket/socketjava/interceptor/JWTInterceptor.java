package com.socket.socketjava.interceptor;

import com.socket.socketjava.domain.vo.LoginVo;
import com.socket.socketjava.utils.exception.socketException;
import com.socket.socketjava.utils.holder.UserHolder;
import com.socket.socketjava.utils.utils.JwtUtil;
import io.jsonwebtoken.Claims;
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
    private JwtUtil jwtUtil; // 确保 JwtUtil 被正确注入

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");

        Claims claims = JwtUtil.parseToken(token);
        String number = claims.get("number", String.class);
        String password = claims.get("password", String.class);
        UserHolder.setLoginVo(new LoginVo(number,password));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.clear();
    }
}
