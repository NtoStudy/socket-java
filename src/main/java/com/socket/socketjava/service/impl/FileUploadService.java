package com.socket.socketjava.service.impl;

import com.socket.socketjava.utils.AliyunOssOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class FileUploadService {

    @Autowired
    private AliyunOssOperator aliyunOssOperator;

    private static final ConcurrentHashMap<String, Map<Integer, byte[]>> tempFileStorage = new ConcurrentHashMap<>();

    public void handleFileChunk(MultipartFile file, String fileName, int chunkNumber, int totalChunks) throws Exception {
        tempFileStorage.computeIfAbsent(fileName, k -> new ConcurrentHashMap<>())
                .put(chunkNumber, file.getBytes());

        if (chunkNumber == totalChunks - 1) {
            rebuildAndUploadFile(fileName, totalChunks);
        }
    }

    private void rebuildAndUploadFile(String fileName, int totalChunks) {
        Map<Integer, byte[]> chunks = tempFileStorage.get(fileName);
        if (chunks == null || chunks.size() != totalChunks) {
            log.error("File chunks incomplete for file: {}", fileName);
            return;
        }

        try {
            byte[] fileData = rebuildFile(chunks);
            uploadToOss(fileData, fileName);
        } finally {
            tempFileStorage.remove(fileName);
        }
    }

    private byte[] rebuildFile(Map<Integer, byte[]> chunks) {
        Map<Integer, byte[]> sortedChunks = new TreeMap<>(chunks);
        byte[] fileData = new byte[0];

        for (byte[] chunk : sortedChunks.values()) {
            byte[] newFileData = new byte[fileData.length + chunk.length];
            System.arraycopy(fileData, 0, newFileData, 0, fileData.length);
            System.arraycopy(chunk, 0, newFileData, fileData.length, chunk.length);
            fileData = newFileData;
        }

        return fileData;
    }

    private void uploadToOss(byte[] fileData, String fileName) {
        String objectName = "uploads/" + UUID.randomUUID() + "-" + fileName;
        String fileUrl = aliyunOssOperator.uploadBytes(fileData, objectName);

        if (fileUrl != null) {
            log.info("File uploaded to OSS: {}", fileUrl);
        } else {
            log.error("Failed to upload file to OSS: {}", fileName);
        }
    }
}