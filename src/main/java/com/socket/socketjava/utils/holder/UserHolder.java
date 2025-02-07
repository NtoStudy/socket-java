package com.socket.socketjava.utils.holder;

import com.socket.socketjava.domain.vo.LoginVo;

public class UserHolder {
    public static ThreadLocal<LoginVo> threadLocal = new ThreadLocal<>();

    public static void setLoginVo(LoginVo loginVo) {
        threadLocal.set(loginVo);
    }

    public static LoginVo getLoginVo() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
