package com.openmanagement.file.vo;

import lombok.Data;

import java.io.InputStream;

@Data
public class FileDownload {

    private InputStream inputStream;
    private String contentType;
    private String fileName;
    private long contentLength;
}
