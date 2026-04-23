package com.openmanagement.oa.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.oa.domain.entity.OaExpenseApply;
import com.openmanagement.oa.mapper.ExpenseApplyMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseApplyServiceImplTest {

    @Mock
    private ExpenseApplyMapper expenseApplyMapper;

    @InjectMocks
    private ExpenseApplyServiceImpl expenseApplyService;

    private OaExpenseApply validApply() {
        OaExpenseApply apply = new OaExpenseApply();
        apply.setApplicantId(1L);
        apply.setExpenseType("TRAVEL");
        apply.setTotalAmount(new BigDecimal("1500.00"));
        return apply;
    }

    // --- submitExpenseApply validation ---

    @Test
    void submitShouldThrowWhenApplyIsNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenApplicantIdIsNull() {
        OaExpenseApply apply = validApply();
        apply.setApplicantId(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenExpenseTypeIsNull() {
        OaExpenseApply apply = validApply();
        apply.setExpenseType(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenExpenseTypeIsBlank() {
        OaExpenseApply apply = validApply();
        apply.setExpenseType("   ");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenTotalAmountIsNull() {
        OaExpenseApply apply = validApply();
        apply.setTotalAmount(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenTotalAmountIsZero() {
        OaExpenseApply apply = validApply();
        apply.setTotalAmount(BigDecimal.ZERO);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenTotalAmountIsNegative() {
        OaExpenseApply apply = validApply();
        apply.setTotalAmount(new BigDecimal("-100.00"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseApplyService.submitExpenseApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    // --- submitExpenseApply success ---

    @Test
    void submitShouldInsertWithGeneratedApplyNumberAndSubmittedStatus() {
        OaExpenseApply apply = validApply();
        ArgumentCaptor<OaExpenseApply> captor = ArgumentCaptor.forClass(OaExpenseApply.class);

        expenseApplyService.submitExpenseApply(apply);

        verify(expenseApplyMapper).insert(captor.capture());
        OaExpenseApply saved = captor.getValue();
        assertNotNull(saved.getApplyNo());
        assertTrue(saved.getApplyNo().startsWith("EX"));
        assertEquals("SUBMITTED", saved.getApplyStatus());
    }

    @Test
    void submitShouldClearIdAndProcessInstanceIdBeforeInsert() {
        OaExpenseApply apply = validApply();
        apply.setId(999L);
        apply.setProcessInstanceId(888L);
        ArgumentCaptor<OaExpenseApply> captor = ArgumentCaptor.forClass(OaExpenseApply.class);

        expenseApplyService.submitExpenseApply(apply);

        verify(expenseApplyMapper).insert(captor.capture());
        assertNull(captor.getValue().getId());
        assertNull(captor.getValue().getProcessInstanceId());
    }

    @Test
    void submitShouldAcceptSmallPositiveAmount() {
        OaExpenseApply apply = validApply();
        apply.setTotalAmount(new BigDecimal("0.01"));

        assertDoesNotThrow(() -> expenseApplyService.submitExpenseApply(apply));
    }
}
