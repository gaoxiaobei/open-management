package com.openmanagement.asset.service.impl;

import com.openmanagement.asset.domain.entity.AssetReceiveApply;
import com.openmanagement.asset.mapper.AssetReceiveApplyMapper;
import com.openmanagement.asset.service.AssetReceiveService;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AssetReceiveServiceImpl implements AssetReceiveService {

    private final AssetReceiveApplyMapper assetReceiveApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReceiveApply(Long assetId, Long applicantId, String reason) {
        if (assetId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "资产ID不能为空");
        }
        if (applicantId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "申请人不能为空");
        }

        AssetReceiveApply apply = new AssetReceiveApply();
        apply.setApplyNo(generateApplyNo("AR"));
        apply.setAssetId(assetId);
        apply.setApplicantId(applicantId);
        apply.setReason(reason);
        apply.setProcessInstanceId(null);
        apply.setApplyStatus(CommonConstants.APPLY_STATUS_SUBMITTED);
        assetReceiveApplyMapper.insert(apply);
    }

    private String generateApplyNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
