package com.socket.socketjava.utils.holder;

import com.socket.socketjava.domain.holder.LoginHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHolder {
    public static ThreadLocal<LoginHolder> threadLocal = new ThreadLocal<>();

   public static void setLoginHolder(LoginHolder loginHolder) {
       log.info("设置用户信息");
       threadLocal.set(loginHolder);
   }

   public static LoginHolder getLoginHolder() {
       return threadLocal.get();
   }


    public static void clear() {
        threadLocal.remove();
    }
}
