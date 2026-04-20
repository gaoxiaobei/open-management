package com.openmanagement.file.controller;

import com.openmanagement.common.result.R;
import com.openmanagement.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(required = false) String bizType,
                            @RequestParam(required = false) Long bizId) {
        return R.ok(fileStorageService.upload(file, bizType, bizId));
    }

    @GetMapping("/{id}/url")
    public R<String> getAccessUrl(@PathVariable Long id) {
        return R.ok(fileStorageService.getAccessUrl(id));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        fileStorageService.delete(id);
        return R.ok();
    }
}
