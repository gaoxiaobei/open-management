package com.openmanagement.oa.service;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaTravelApply;

public interface TravelApplyService {

    void submitTravelApply(OaTravelApply apply);

    PageResult<OaTravelApply> myApplies(Long applicantId, PageQuery pageQuery);
}
