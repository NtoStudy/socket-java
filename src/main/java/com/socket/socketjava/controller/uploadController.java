package com.socket.socketjava.controller;

import com.socket.socketjava.result.Result;
import com.socket.socketjava.utils.AliyunOssOperator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name="上传文件")
public class uploadController {
    @Autowired
    private AliyunOssOperator aliyunOssOperator;

    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 将上传的文件转换为 File 对象
            File fileToUpload = convertMultiPartToFile(file);

            // 调用工具类上传文件
            return Result.ok(aliyunOssOperator.uploadFile(fileToUpload));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail()    ;
        }
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        return new File(multipartFile.getOriginalFilename());
    }
}
