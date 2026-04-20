package com.openmanagement.oa.service;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaExpenseApply;

public interface ExpenseApplyService {

    void submitExpenseApply(OaExpenseApply apply);

    PageResult<OaExpenseApply> myApplies(Long applicantId, PageQuery pageQuery);
}
