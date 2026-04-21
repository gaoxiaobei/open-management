package com.openmanagement.oa.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.constant.CommonConstants;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.oa.domain.entity.OaExpenseApply;
import com.openmanagement.oa.service.ExpenseApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oa/expense-applications")
@RequiredArgsConstructor
public class ExpenseApplyController {

    private final ExpenseApplyService expenseApplyService;

    @PostMapping
    public R<Void> submit(@RequestBody OaExpenseApply apply) {
        apply.setApplicantId(requireCurrentUserId());
        expenseApplyService.submitExpenseApply(apply);
        return R.ok();
    }

    @GetMapping("/my")
    public R<PageResult<OaExpenseApply>> myApplies(@RequestParam Long applicantId, PageQuery pageQuery) {
        ensureSelfOrAdmin(applicantId);
        return R.ok(expenseApplyService.myApplies(applicantId, pageQuery));
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
