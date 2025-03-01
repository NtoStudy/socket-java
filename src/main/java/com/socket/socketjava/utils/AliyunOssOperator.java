package com.socket.socketjava.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Component
public class AliyunOssOperator {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public String uploadFile(File file) throws IOException {
        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 读取文件内容
            byte[] fileContent = Files.readAllBytes(file.toPath());

            // 上传文件
            String objectName = file.getName();
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(fileContent));

            // 返回文件访问 URL
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭 OSS 客户端
            ossClient.shutdown();
        }
    }

    public String uploadBytes(byte[] content, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 将 byte[] 转换为输入流
            InputStream inputStream = new ByteArrayInputStream(content);

            // 上传文件
            ossClient.putObject(bucketName, objectName, inputStream);

            // 返回文件访问 URL
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭 OSS 客户端
            ossClient.shutdown();
        }
    }

}