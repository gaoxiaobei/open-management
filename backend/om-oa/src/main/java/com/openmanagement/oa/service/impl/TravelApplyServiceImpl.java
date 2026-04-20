package com.openmanagement.oa.service.impl;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.oa.domain.entity.OaTravelApply;
import com.openmanagement.oa.service.TravelApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TravelApplyServiceImpl implements TravelApplyService {

    @Override
    public void submitTravelApply(OaTravelApply apply) {
        // TODO: generate applyNo, persist apply, start travel approval workflow
    }

    @Override
    public PageResult<OaTravelApply> myApplies(Long applicantId, PageQuery pageQuery) {
        // TODO: query travel applies for applicantId with pagination
        return PageResult.of(0L, Collections.emptyList());
    }
}
