package com.socket.socketjava.utils.holder;

import com.socket.socketjava.domain.holder.LoginHolder;

public class UserHolder {
    public static ThreadLocal<LoginHolder> threadLocal = new ThreadLocal<>();

    public static void setLoginHolder(LoginHolder loginHolder) {
        threadLocal.set(loginHolder);
    }

    public static LoginHolder getLoginHolder() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
