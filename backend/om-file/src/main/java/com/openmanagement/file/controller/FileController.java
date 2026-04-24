package com.openmanagement.file.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.file.service.FileStorageService;
import com.openmanagement.file.vo.FileDownload;
import com.openmanagement.file.vo.FileVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private static final int BUFFER_SIZE = 8192;

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(required = false) String bizType,
                            @RequestParam(required = false) Long bizId) {
        return R.ok(fileStorageService.upload(file, bizType, bizId));
    }

    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        FileDownload fileDownload = fileStorageService.download(id);
        String encodedName = URLEncoder.encode(fileDownload.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setContentType(fileDownload.getContentType());
        response.setContentLengthLong(fileDownload.getContentLength());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileDownload.getFileName() + "\"; filename*=UTF-8''" + encodedName);

        try (InputStream in = fileDownload.getInputStream();
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }

    @GetMapping
    public R<List<FileVO>> list() {
        return R.ok(fileStorageService.listFiles());
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        fileStorageService.delete(id);
        return R.ok();
    }
}
