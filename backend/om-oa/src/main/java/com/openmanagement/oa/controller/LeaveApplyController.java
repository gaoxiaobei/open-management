package com.openmanagement.oa.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
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
        apply.setApplicantId(requireCurrentUserId());
        leaveApplyService.submitLeaveApply(apply);
        return R.ok();
    }

    @GetMapping("/my")
    public R<PageResult<OaLeaveApply>> myApplies(@RequestParam(required = false) Long applicantId, PageQuery pageQuery) {
        Long targetId = applicantId != null ? applicantId : requireCurrentUserId();
        ensureSelfOrAdmin(targetId);
        return R.ok(leaveApplyService.myApplies(targetId, pageQuery));
    }

    private Long requireCurrentUserId() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
        }
        return currentUserId;
    }

    private void ensureSelfOrAdmin(Long applicantId) {
        Long currentUserId = requireCurrentUserId();
        if (!CommonConstants.ADMIN_USER_ID.equals(currentUserId) && !currentUserId.equals(applicantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
        }
    }
}
