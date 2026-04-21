package com.openmanagement.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaExpenseApply;
import com.openmanagement.oa.mapper.ExpenseApplyMapper;
import com.openmanagement.oa.service.ExpenseApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ExpenseApplyServiceImpl implements ExpenseApplyService {

    private final ExpenseApplyMapper expenseApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitExpenseApply(OaExpenseApply apply) {
        validateApply(apply);
        apply.setId(null);
        apply.setApplyNo(generateApplyNo("EX"));
        apply.setProcessInstanceId(null);
        apply.setApplyStatus(CommonConstants.APPLY_STATUS_SUBMITTED);
        expenseApplyMapper.insert(apply);
    }

    @Override
    public PageResult<OaExpenseApply> myApplies(Long applicantId, PageQuery pageQuery) {
        Page<OaExpenseApply> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<OaExpenseApply> result = expenseApplyMapper.selectPage(page, new LambdaQueryWrapper<OaExpenseApply>()
                .eq(OaExpenseApply::getApplicantId, applicantId)
                .orderByDesc(OaExpenseApply::getCreatedAt, OaExpenseApply::getId));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    private void validateApply(OaExpenseApply apply) {
        if (apply == null || apply.getApplicantId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "申请人不能为空");
        }
        if (apply.getExpenseType() == null || apply.getExpenseType().isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "费用类型不能为空");
        }
        if (apply.getTotalAmount() == null || apply.getTotalAmount().signum() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "报销金额必须大于0");
        }
    }

    private String generateApplyNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
