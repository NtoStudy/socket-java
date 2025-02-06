package com.socket.socketjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.socket.socketjava.mapper")
public class SocketJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketJavaApplication.class, args);
    }

}
