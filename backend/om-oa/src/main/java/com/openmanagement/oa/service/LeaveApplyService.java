package com.openmanagement.oa.service;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaLeaveApply;

public interface LeaveApplyService {

    void submitLeaveApply(OaLeaveApply apply);

    PageResult<OaLeaveApply> myApplies(Long applicantId, PageQuery pageQuery);
}
