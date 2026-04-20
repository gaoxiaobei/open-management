package com.openmanagement.asset.controller;

import com.openmanagement.asset.service.AssetReceiveService;
import com.openmanagement.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assets/receive-applications")
@RequiredArgsConstructor
public class AssetReceiveController {

    private final AssetReceiveService assetReceiveService;

    @PostMapping
    public R<Void> submit(@RequestBody Map<String, Object> body) {
        Long assetId = Long.valueOf(body.get("assetId").toString());
        Long applicantId = Long.valueOf(body.get("applicantId").toString());
        String reason = (String) body.get("reason");
        assetReceiveService.submitReceiveApply(assetId, applicantId, reason);
        return R.ok();
    }
}
