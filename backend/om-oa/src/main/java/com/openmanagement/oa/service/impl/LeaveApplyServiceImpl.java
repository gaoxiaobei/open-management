package com.openmanagement.oa.service.impl;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaLeaveApply;
import com.openmanagement.oa.service.LeaveApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LeaveApplyServiceImpl implements LeaveApplyService {

    @Override
    public void submitLeaveApply(OaLeaveApply apply) {
        // TODO: generate applyNo, persist apply, start leave approval workflow
    }

    @Override
    public PageResult<OaLeaveApply> myApplies(Long applicantId, PageQuery pageQuery) {
        // TODO: query leave applies for applicantId with pagination
        return PageResult.of(0L, Collections.emptyList());
    }
}
