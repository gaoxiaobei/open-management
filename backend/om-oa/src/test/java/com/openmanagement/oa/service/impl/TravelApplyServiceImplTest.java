package com.openmanagement.oa.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.oa.domain.entity.OaTravelApply;
import com.openmanagement.oa.mapper.TravelApplyMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelApplyServiceImplTest {

    @Mock
    private TravelApplyMapper travelApplyMapper;

    @InjectMocks
    private TravelApplyServiceImpl travelApplyService;

    private OaTravelApply validApply() {
        OaTravelApply apply = new OaTravelApply();
        apply.setApplicantId(1L);
        apply.setDestination("Shanghai");
        apply.setStartDate(LocalDate.of(2026, 5, 10));
        apply.setEndDate(LocalDate.of(2026, 5, 15));
        apply.setTravelDays(5);
        apply.setPurpose("Client meeting");
        return apply;
    }

    // --- submitTravelApply validation ---

    @Test
    void submitShouldThrowWhenApplyIsNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenApplicantIdIsNull() {
        OaTravelApply apply = validApply();
        apply.setApplicantId(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenDestinationIsNull() {
        OaTravelApply apply = validApply();
        apply.setDestination(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenDestinationIsBlank() {
        OaTravelApply apply = validApply();
        apply.setDestination("   ");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenStartDateIsNull() {
        OaTravelApply apply = validApply();
        apply.setStartDate(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenEndDateIsNull() {
        OaTravelApply apply = validApply();
        apply.setEndDate(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenEndDateBeforeStartDate() {
        OaTravelApply apply = validApply();
        apply.setEndDate(apply.getStartDate().minusDays(1));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenTravelDaysIsNull() {
        OaTravelApply apply = validApply();
        apply.setTravelDays(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenTravelDaysIsZero() {
        OaTravelApply apply = validApply();
        apply.setTravelDays(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void submitShouldThrowWhenTravelDaysIsNegative() {
        OaTravelApply apply = validApply();
        apply.setTravelDays(-3);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> travelApplyService.submitTravelApply(apply));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    // --- submitTravelApply success ---

    @Test
    void submitShouldInsertWithGeneratedApplyNumberAndSubmittedStatus() {
        OaTravelApply apply = validApply();
        ArgumentCaptor<OaTravelApply> captor = ArgumentCaptor.forClass(OaTravelApply.class);

        travelApplyService.submitTravelApply(apply);

        verify(travelApplyMapper).insert(captor.capture());
        OaTravelApply saved = captor.getValue();
        assertNotNull(saved.getApplyNo());
        assertTrue(saved.getApplyNo().startsWith("TR"));
        assertEquals("SUBMITTED", saved.getApplyStatus());
    }

    @Test
    void submitShouldClearIdAndProcessInstanceIdBeforeInsert() {
        OaTravelApply apply = validApply();
        apply.setId(999L);
        apply.setProcessInstanceId(888L);
        ArgumentCaptor<OaTravelApply> captor = ArgumentCaptor.forClass(OaTravelApply.class);

        travelApplyService.submitTravelApply(apply);

        verify(travelApplyMapper).insert(captor.capture());
        assertNull(captor.getValue().getId());
        assertNull(captor.getValue().getProcessInstanceId());
    }

    @Test
    void submitShouldAllowEndDateEqualToStartDate() {
        OaTravelApply apply = validApply();
        apply.setEndDate(apply.getStartDate());

        assertDoesNotThrow(() -> travelApplyService.submitTravelApply(apply));
    }
}
