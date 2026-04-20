package com.openmanagement.asset.service;

public interface AssetReceiveService {

    void submitReceiveApply(Long assetId, Long applicantId, String reason);
}
