package com.openmanagement.oa.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.oa.domain.entity.OaTravelApply;
import com.openmanagement.oa.service.TravelApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oa/travel-applications")
@RequiredArgsConstructor
public class TravelApplyController {

    private final TravelApplyService travelApplyService;

    @PostMapping
    public R<Void> submit(@RequestBody OaTravelApply apply) {
        travelApplyService.submitTravelApply(apply);
        return R.ok();
    }

    @GetMapping("/my")
    public R<PageResult<OaTravelApply>> myApplies(@RequestParam Long applicantId, PageQuery pageQuery) {
        return R.ok(travelApplyService.myApplies(applicantId, pageQuery));
    }
}
