package com.socket.socketjava.controller;

import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.impl.FileUploadService;
import com.socket.socketjava.utils.AliyunOssOperator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "文件上传服务")
public class uploadController {


    @Autowired
    private AliyunOssOperator aliyunOssOperator;
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    @Operation(summary = "上传单个文件")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 将上传的文件转换为 File 对象
            File fileToUpload = convertMultiPartToFile(file);

            // 调用工具类上传文件
            return Result.ok(aliyunOssOperator.uploadFile(fileToUpload));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @PostMapping("/upload/batch")
    @Operation(summary = "批量上传多个文件") 
    public Result uploadFiles(@RequestParam("files") MultipartFile[] files){
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                File fileToUpload = convertMultiPartToFile(file);
                String url = aliyunOssOperator.uploadFile(fileToUpload);
                urls.add(url);
            }
            return Result.ok(urls);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        // 创建一个临时文件
        File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID() + "-" + file.getOriginalFilename());
        // 将 MultipartFile 的内容写入临时文件
        file.transferTo(tempFile);
        return tempFile;
    }

    @PostMapping("/upload/chunk")
    @Operation(summary = "分片上传大文件")
    public ResponseEntity<String> uploadFileChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks) {
        try {
            fileUploadService.handleFileChunk(file, fileName, chunkNumber, totalChunks);
            return ResponseEntity.ok("Chunk uploaded successfully");
        } catch (Exception e) {
            log.error("Error while uploading chunk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed");
        }
    }



}
