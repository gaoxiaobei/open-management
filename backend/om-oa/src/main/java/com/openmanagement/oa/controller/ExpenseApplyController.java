package com.openmanagement.oa.controller;

import com.openmanagement.common.base.PageQuery;
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
        expenseApplyService.submitExpenseApply(apply);
        return R.ok();
    }

    @GetMapping("/my")
    public R<PageResult<OaExpenseApply>> myApplies(@RequestParam Long applicantId, PageQuery pageQuery) {
        return R.ok(expenseApplyService.myApplies(applicantId, pageQuery));
    }
}
