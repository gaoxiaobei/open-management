package com.openmanagement.file.service;

import com.openmanagement.file.vo.FileDownload;
import com.openmanagement.file.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    String upload(MultipartFile file, String bizType, Long bizId);

    void delete(Long fileId);

    FileDownload download(Long fileId);

    List<FileVO> listFiles();
}
