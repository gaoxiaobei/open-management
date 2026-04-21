package com.openmanagement.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.util.ApplicationNoGenerator;
import com.openmanagement.oa.domain.entity.OaLeaveApply;
import com.openmanagement.oa.mapper.LeaveApplyMapper;
import com.openmanagement.oa.service.LeaveApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveApplyServiceImpl implements LeaveApplyService {

    private final LeaveApplyMapper leaveApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitLeaveApply(OaLeaveApply apply) {
        validateApply(apply);
        apply.setId(null);
        apply.setApplyNo(ApplicationNoGenerator.next("LV"));
        apply.setProcessInstanceId(null);
        apply.setApplyStatus(CommonConstants.APPLY_STATUS_SUBMITTED);
        leaveApplyMapper.insert(apply);
    }

    @Override
    public PageResult<OaLeaveApply> myApplies(Long applicantId, PageQuery pageQuery) {
        Page<OaLeaveApply> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<OaLeaveApply> result = leaveApplyMapper.selectPage(page, new LambdaQueryWrapper<OaLeaveApply>()
                .eq(OaLeaveApply::getApplicantId, applicantId)
                .orderByDesc(OaLeaveApply::getCreatedAt, OaLeaveApply::getId));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    private void validateApply(OaLeaveApply apply) {
        if (apply == null || apply.getApplicantId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "申请人不能为空");
        }
        if (apply.getLeaveType() == null || apply.getLeaveType().isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "请假类型不能为空");
        }
        if (apply.getStartTime() == null || apply.getEndTime() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "请假时间不能为空");
        }
        if (apply.getEndTime().isBefore(apply.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "结束时间不能早于开始时间");
        }
        if (apply.getLeaveDays() == null || apply.getLeaveDays().signum() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "请假天数必须大于0");
        }
    }

}
