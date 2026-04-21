package com.openmanagement.asset.controller;

import com.openmanagement.asset.dto.AssetReceiveApplyRequest;
import com.openmanagement.asset.service.AssetReceiveService;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets/receive-applications")
@RequiredArgsConstructor
public class AssetReceiveController {

    private final AssetReceiveService assetReceiveService;

    @PostMapping
    public R<Void> submit(@Valid @RequestBody AssetReceiveApplyRequest request) {
        assetReceiveService.submitReceiveApply(request.getAssetId(), requireCurrentUserId(), request.getReason());
        return R.ok();
    }

    private Long requireCurrentUserId() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
        }
        return currentUserId;
    }
}
