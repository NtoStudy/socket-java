package com.socket.socketjava.utils;

import com.socket.socketjava.result.ResultCodeEnum;

public class socketException extends RuntimeException{
    private Integer code;

    public socketException(Integer code, String message) {
        super(message);
        this.code = code;
    }


    public socketException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
