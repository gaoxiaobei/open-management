package com.openmanagement.oa.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
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
        apply.setApplicantId(requireCurrentUserId());
        travelApplyService.submitTravelApply(apply);
        return R.ok();
    }

    @GetMapping("/my")
    public R<PageResult<OaTravelApply>> myApplies(@RequestParam Long applicantId, PageQuery pageQuery) {
        ensureSelfOrAdmin(applicantId);
        return R.ok(travelApplyService.myApplies(applicantId, pageQuery));
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
