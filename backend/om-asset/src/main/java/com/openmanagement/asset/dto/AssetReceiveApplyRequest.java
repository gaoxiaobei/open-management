package com.openmanagement.asset.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetReceiveApplyRequest {

    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    private String reason;
}
