package com.openmanagement.asset.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.openmanagement.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_info")
public class AssetInfo extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String assetCode;

    private String assetName;

    private String category;

    private String brand;

    private String model;

    private LocalDate purchaseDate;

    private BigDecimal purchasePrice;

    private String assetStatus;

    private String location;

    private Long currentUserId;

    private Long deptId;

    private String remark;
}
