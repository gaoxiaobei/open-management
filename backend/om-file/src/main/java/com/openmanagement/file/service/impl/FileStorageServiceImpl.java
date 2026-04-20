package com.openmanagement.file.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.file.domain.entity.SysFile;
import com.openmanagement.file.mapper.FileMapper;
import com.openmanagement.file.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final int PRESIGNED_URL_EXPIRE_SECONDS = 60 * 60;
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final MinioClient minioClient;
    private final FileMapper fileMapper;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upload(MultipartFile file, String bizType, Long bizId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "上传文件不能为空");
        }

        String originalName = StringUtils.hasText(file.getOriginalFilename())
                ? file.getOriginalFilename()
                : "unknown";
        String fileName = buildStoredFileName(originalName);
        String filePath = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "/" + fileName;

        ensureBucketExists();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(resolveContentType(file.getContentType()))
                            .build());

            SysFile entity = new SysFile();
            entity.setFileName(fileName);
            entity.setOriginalName(originalName);
            entity.setFilePath(filePath);
            entity.setFileSize(file.getSize());
            entity.setMimeType(resolveContentType(file.getContentType()));
            entity.setBizType(bizType);
            entity.setBizId(bizId);
            fileMapper.insert(entity);

            return buildAccessUrl(entity);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL.getCode(), ErrorCode.FILE_UPLOAD_FAIL.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long fileId) {
        SysFile file = requireFile(fileId);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getFilePath())
                            .build());
            fileMapper.deleteById(fileId);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "文件删除失败", e);
        }
    }

    @Override
    public String getAccessUrl(Long fileId) {
        SysFile file = requireFile(fileId);
        return buildAccessUrl(file);
    }

    private SysFile requireFile(Long fileId) {
        if (fileId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "文件ID不能为空");
        }
        SysFile file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND.getCode(), ErrorCode.FILE_NOT_FOUND.getMessage());
        }
        return file;
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "初始化 MinIO Bucket 失败", e);
        }
    }

    private String buildAccessUrl(SysFile file) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(file.getFilePath())
                    .build());
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(file.getFilePath())
                            .extraQueryParams(buildDownloadQuery(file))
                            .expiry(PRESIGNED_URL_EXPIRE_SECONDS)
                            .build());
        } catch (ErrorResponseException e) {
            if (e.errorResponse() != null && "NoSuchKey".equals(e.errorResponse().code())) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND.getCode(), ErrorCode.FILE_NOT_FOUND.getMessage());
            }
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "获取文件访问地址失败", e);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "获取文件访问地址失败", e);
        }
    }

    private Map<String, String> buildDownloadQuery(SysFile file) {
        String originalName = Objects.requireNonNullElse(file.getOriginalName(), file.getFileName());
        String encodedName = URLEncoder.encode(originalName, StandardCharsets.UTF_8).replace("+", "%20");
        return Map.of("response-content-disposition", "attachment; filename*=UTF-8''" + encodedName);
    }

    private String buildStoredFileName(String originalName) {
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > -1 && dotIndex < originalName.length() - 1) {
            extension = originalName.substring(dotIndex);
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    private String resolveContentType(String contentType) {
        return StringUtils.hasText(contentType) ? contentType : DEFAULT_CONTENT_TYPE;
    }
}
