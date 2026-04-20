package com.openmanagement.asset.service.impl;

import com.openmanagement.asset.service.AssetReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetReceiveServiceImpl implements AssetReceiveService {

    @Override
    public void submitReceiveApply(Long assetId, Long applicantId, String reason) {
        // TODO: generate applyNo, persist AssetReceiveApply, start asset receive workflow
    }
}
