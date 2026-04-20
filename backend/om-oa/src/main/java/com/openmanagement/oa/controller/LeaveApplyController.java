package com.openmanagement.oa.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.oa.domain.entity.OaLeaveApply;
import com.openmanagement.oa.service.LeaveApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oa/leave-applications")
@RequiredArgsConstructor
public class LeaveApplyController {

    private final LeaveApplyService leaveApplyService;

    @PostMapping
    public R<Void> submit(@RequestBody OaLeaveApply apply) {
        leaveApplyService.submitLeaveApply(apply);
        return R.ok();
    }

    @GetMapping("/my")
    public R<PageResult<OaLeaveApply>> myApplies(@RequestParam Long applicantId, PageQuery pageQuery) {
        return R.ok(leaveApplyService.myApplies(applicantId, pageQuery));
    }
}
