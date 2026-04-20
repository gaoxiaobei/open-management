package com.openmanagement.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String upload(MultipartFile file, String bizType, Long bizId);

    void delete(Long fileId);

    String getAccessUrl(Long fileId);
}
