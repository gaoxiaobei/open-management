package com.openmanagement.oa.service.impl;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.oa.domain.entity.OaLeaveApply;
import com.openmanagement.oa.mapper.LeaveApplyMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveApplyServiceImplTest {

    @Mock
    private LeaveApplyMapper leaveApplyMapper;

    @InjectMocks
    private LeaveApplyServiceImpl leaveApplyService;

    private OaLeaveApply validApply() {
        OaLeaveApply apply = new OaLeaveApply();
        apply.setApplicantId(1L);
        apply.setLeaveType("ANNUAL");
        apply.setStartTime(LocalDateTime.of(2026, 5, 1, 9, 0));
        apply.setEndTime(LocalDateTime.of(2026, 5, 3, 18, 0));
        apply.setLeaveDays(new BigDecimal("2.5"));
        return apply;
    }

    // --- submitLeaveApply validation ---

    @Test
    void submitShouldThrowWhenApplyIsNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenApplicantIdIsNull() {
        OaLeaveApply apply = validApply();
        apply.setApplicantId(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenLeaveTypeIsNull() {
        OaLeaveApply apply = validApply();
        apply.setLeaveType(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenLeaveTypeIsBlank() {
        OaLeaveApply apply = validApply();
        apply.setLeaveType("   ");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenStartTimeIsNull() {
        OaLeaveApply apply = validApply();
        apply.setStartTime(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenEndTimeIsNull() {
        OaLeaveApply apply = validApply();
        apply.setEndTime(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenEndTimeBeforeStartTime() {
        OaLeaveApply apply = validApply();
        apply.setEndTime(apply.getStartTime().minusDays(1));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenLeaveDaysIsNull() {
        OaLeaveApply apply = validApply();
        apply.setLeaveDays(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenLeaveDaysIsZero() {
        OaLeaveApply apply = validApply();
        apply.setLeaveDays(BigDecimal.ZERO);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenLeaveDaysIsNegative() {
        OaLeaveApply apply = validApply();
        apply.setLeaveDays(new BigDecimal("-1"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> leaveApplyService.submitLeaveApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    // --- submitLeaveApply success ---

    @Test
    void submitShouldInsertWithGeneratedApplyNumberAndSubmittedStatus() {
        OaLeaveApply apply = validApply();
        ArgumentCaptor<OaLeaveApply> captor = ArgumentCaptor.forClass(OaLeaveApply.class);

        leaveApplyService.submitLeaveApply(apply);

        verify(leaveApplyMapper).insert(captor.capture());
        OaLeaveApply saved = captor.getValue();
        assertNotNull(saved.getApplyNo());
        assertTrue(saved.getApplyNo().startsWith("LV"));
        assertEquals("SUBMITTED", saved.getApplyStatus());
    }

    @Test
    void submitShouldClearIdAndProcessInstanceIdBeforeInsert() {
        OaLeaveApply apply = validApply();
        apply.setId(999L);
        apply.setProcessInstanceId(888L);
        ArgumentCaptor<OaLeaveApply> captor = ArgumentCaptor.forClass(OaLeaveApply.class);

        leaveApplyService.submitLeaveApply(apply);

        verify(leaveApplyMapper).insert(captor.capture());
        assertNull(captor.getValue().getId());
        assertNull(captor.getValue().getProcessInstanceId());
    }

    @Test
    void submitShouldAllowEndTimeEqualToStartTime() {
        OaLeaveApply apply = validApply();
        apply.setEndTime(apply.getStartTime());

        assertDoesNotThrow(() -> leaveApplyService.submitLeaveApply(apply));
    }
}
