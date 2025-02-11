package com.socket.socketjava.interceptor;


import com.socket.socketjava.domain.holder.LoginHolder;
import com.socket.socketjava.result.ResultCodeEnum;
import com.socket.socketjava.utils.exception.socketException;
import com.socket.socketjava.utils.holder.UserHolder;
import com.socket.socketjava.utils.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //放行OPTIONS请求
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            log.info("放行请求头：{}",method);
            return true;
        }


        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new socketException(ResultCodeEnum.TOKEN_EXPIRED); // 自定义异常
        }
        log.info("拦截器{}",request);
        log.info("拦截器获取token：{}", token);
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = claims.get("userId", Integer.class);
        String number = claims.get("number", String.class);
        UserHolder.setLoginHolder(new LoginHolder(userId, number));
        log.info("拦截器获取用户信息：{}", UserHolder.getLoginHolder().getUserId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
