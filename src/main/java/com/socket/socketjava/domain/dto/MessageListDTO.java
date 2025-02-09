package com.socket.socketjava.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageListDTO<T> {
    private long total;       // 总记录数
    private List<T> list;     // 当前页数据
    private int pageNum;      // 当前页码
    private int pageSize;     // 每页记录数
    private Long startRow;     // 当前页起始记录数
    private Long endRow;       // 当前页结束记录数
    private int pages;        // 总页数
}
