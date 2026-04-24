package com.openmanagement.file.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.file.domain.entity.SysFile;
import com.openmanagement.file.mapper.FileMapper;
import com.openmanagement.file.service.FileStorageService;
import com.openmanagement.file.vo.FileDownload;
import com.openmanagement.file.vo.FileVO;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

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

            return "/api/files/" + entity.getId() + "/download";
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
    public FileDownload download(Long fileId) {
        SysFile file = requireFile(fileId);
        try {
            io.minio.StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getFilePath())
                            .build());

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getFilePath())
                            .build());

            FileDownload dl = new FileDownload();
            dl.setInputStream(stream);
            dl.setContentType(stat.contentType() != null ? stat.contentType() : DEFAULT_CONTENT_TYPE);
            dl.setFileName(Objects.requireNonNullElse(file.getOriginalName(), file.getFileName()));
            dl.setContentLength(stat.size());
            return dl;
        } catch (ErrorResponseException e) {
            if (e.errorResponse() != null && "NoSuchKey".equals(e.errorResponse().code())) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND.getCode(), ErrorCode.FILE_NOT_FOUND.getMessage());
            }
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "获取文件失败", e);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR.getCode(), "获取文件失败", e);
        }
    }

    @Override
    public List<FileVO> listFiles() {
        return fileMapper.selectList(null).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private FileVO toVO(SysFile entity) {
        FileVO vo = new FileVO();
        vo.setId(entity.getId());
        vo.setOriginalName(entity.getOriginalName());
        vo.setContentType(entity.getMimeType());
        vo.setBizType(entity.getBizType());
        vo.setBizId(entity.getBizId());
        vo.setFileSize(entity.getFileSize());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
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
