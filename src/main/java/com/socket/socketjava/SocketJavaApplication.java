package com.socket.socketjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO后端从始至终都有一个逻辑是错了，当加上好友之后，应该是向friend表中添加两条数据，而不是一条，这样就不会有其他的各种问题了，在后续开发中再更改
@SpringBootApplication
@MapperScan("com.socket.socketjava.mapper")
public class SocketJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketJavaApplication.class, args);
    }

}
