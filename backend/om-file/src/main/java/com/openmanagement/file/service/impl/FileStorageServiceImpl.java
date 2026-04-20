package com.openmanagement.file.service.impl;

import com.openmanagement.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public String upload(MultipartFile file, String bizType, Long bizId) {
        // TODO: upload file to MinIO, persist SysFile record, return file URL
        return null;
    }

    @Override
    public void delete(Long fileId) {
        // TODO: delete file from MinIO and remove SysFile record
    }

    @Override
    public String getAccessUrl(Long fileId) {
        // TODO: generate pre-signed URL from MinIO for the given fileId
        return null;
    }
}
