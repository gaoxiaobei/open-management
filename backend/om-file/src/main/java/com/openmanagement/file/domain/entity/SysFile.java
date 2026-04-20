package com.openmanagement.file.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String originalName;

    private String filePath;

    private Long fileSize;

    private String mimeType;

    private String bizType;

    private Long bizId;
}
