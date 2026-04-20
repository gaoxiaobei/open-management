package com.openmanagement.oa.service.impl;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaExpenseApply;
import com.openmanagement.oa.service.ExpenseApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ExpenseApplyServiceImpl implements ExpenseApplyService {

    @Override
    public void submitExpenseApply(OaExpenseApply apply) {
        // TODO: generate applyNo, persist apply, start expense approval workflow
    }

    @Override
    public PageResult<OaExpenseApply> myApplies(Long applicantId, PageQuery pageQuery) {
        // TODO: query expense applies for applicantId with pagination
        return PageResult.of(0L, Collections.emptyList());
    }
}
