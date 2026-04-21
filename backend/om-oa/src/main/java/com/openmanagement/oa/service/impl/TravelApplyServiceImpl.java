package com.openmanagement.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.util.ApplicationNoGenerator;
import com.openmanagement.oa.domain.entity.OaTravelApply;
import com.openmanagement.oa.mapper.TravelApplyMapper;
import com.openmanagement.oa.service.TravelApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TravelApplyServiceImpl implements TravelApplyService {

    private final TravelApplyMapper travelApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitTravelApply(OaTravelApply apply) {
        validateApply(apply);
        apply.setId(null);
        apply.setApplyNo(ApplicationNoGenerator.next("TR"));
        apply.setProcessInstanceId(null);
        apply.setApplyStatus(CommonConstants.APPLY_STATUS_SUBMITTED);
        travelApplyMapper.insert(apply);
    }

    @Override
    public PageResult<OaTravelApply> myApplies(Long applicantId, PageQuery pageQuery) {
        Page<OaTravelApply> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<OaTravelApply> result = travelApplyMapper.selectPage(page, new LambdaQueryWrapper<OaTravelApply>()
                .eq(OaTravelApply::getApplicantId, applicantId)
                .orderByDesc(OaTravelApply::getCreatedAt, OaTravelApply::getId));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    private void validateApply(OaTravelApply apply) {
        if (apply == null || apply.getApplicantId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "申请人不能为空");
        }
        if (apply.getDestination() == null || apply.getDestination().isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "出差目的地不能为空");
        }
        if (apply.getStartDate() == null || apply.getEndDate() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "出差日期不能为空");
        }
        if (apply.getEndDate().isBefore(apply.getStartDate())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "返回日期不能早于出发日期");
        }
        if (apply.getTravelDays() == null || apply.getTravelDays() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "出差天数必须大于0");
        }
    }

}
