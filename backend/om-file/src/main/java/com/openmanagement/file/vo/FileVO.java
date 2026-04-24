package com.openmanagement.file.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileVO {

    private Long id;

    private String originalName;

    private String contentType;

    private String bizType;

    private Long bizId;

    private Long fileSize;

    private LocalDateTime createdAt;
}
