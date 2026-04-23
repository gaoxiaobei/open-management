package com.openmanagement.hr.service.impl;

import com.openmanagement.common.enums.ErrorCode;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.hr.domain.entity.HrEmployee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    private EmployeeServiceImpl makeService() {
        return spy(new EmployeeServiceImpl());
    }

    private HrEmployee validEmployee() {
        HrEmployee emp = new HrEmployee();
        emp.setEmpNo("E001");
        emp.setEmpName("Zhang San");
        emp.setDeptId(10L);
        emp.setHireDate(LocalDate.of(2024, 1, 15));
        return emp;
    }

    // --- createEmployee validation ---

    @Test
    void createShouldThrowWhenEmployeeIsNull() {
        EmployeeServiceImpl service = makeService();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenEmpNoIsNull() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setEmpNo(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(emp));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenEmpNoIsBlank() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setEmpNo("  ");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(emp));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenEmpNameIsNull() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setEmpName(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(emp));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenDeptIdIsNull() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setDeptId(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(emp));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenHireDateIsNull() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setHireDate(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(emp));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void createShouldThrowWhenEmpNoAlreadyExists() {
        EmployeeServiceImpl service = makeService();
        doReturn(1L).when(service).count(any());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createEmployee(validEmployee()));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
        verify(service, never()).save(any());
    }

    @Test
    void createShouldSaveEmployeeWithNullId() {
        EmployeeServiceImpl service = makeService();
        doReturn(0L).when(service).count(any());
        doReturn(true).when(service).save(any(HrEmployee.class));

        HrEmployee emp = validEmployee();
        emp.setId(999L);

        service.createEmployee(emp);

        verify(service).save(argThat(e -> e.getId() == null));
    }

    // --- updateEmployee ---

    @Test
    void updateShouldThrowWhenEmployeeIsNull() {
        EmployeeServiceImpl service = makeService();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateEmployee(1L, null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void updateShouldThrowWhenEmployeeNotFound() {
        EmployeeServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateEmployee(99L, validEmployee()));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void updateShouldThrowWhenNewEmpNoAlreadyExistsOnAnotherEmployee() {
        EmployeeServiceImpl service = makeService();
        HrEmployee existing = validEmployee();
        existing.setId(1L);
        existing.setEmpNo("E001");
        doReturn(existing).when(service).getById(1L);
        doReturn(1L).when(service).count(any()); // duplicate found

        HrEmployee update = new HrEmployee();
        update.setEmpNo("E002"); // different from existing

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateEmployee(1L, update));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void updateShouldAllowKeepingSameEmpNo() {
        EmployeeServiceImpl service = makeService();
        HrEmployee existing = validEmployee();
        existing.setId(1L);
        existing.setEmpNo("E001");
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        HrEmployee update = new HrEmployee();
        update.setEmpNo("E001"); // same as existing — should not check uniqueness

        assertDoesNotThrow(() -> service.updateEmployee(1L, update));
        verify(service, never()).count(any()); // uniqueness check skipped
    }

    @Test
    void updateShouldSetIdBeforeUpdate() {
        EmployeeServiceImpl service = makeService();
        HrEmployee existing = validEmployee();
        existing.setId(1L);
        doReturn(existing).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        HrEmployee update = new HrEmployee();
        update.setEmpName("New Name");

        service.updateEmployee(1L, update);

        verify(service).updateById(argThat(e -> Long.valueOf(1L).equals(e.getId())));
    }

    // --- changeEmpStatus ---

    @Test
    void changeStatusShouldThrowWhenEmployeeNotFound() {
        EmployeeServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changeEmpStatus(99L, "RESIGNED"));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void changeStatusShouldThrowWhenStatusIsBlank() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setId(1L);
        doReturn(emp).when(service).getById(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changeEmpStatus(1L, "  "));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void changeStatusShouldUpdateEmpStatusField() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setId(1L);
        emp.setEmpStatus("ACTIVE");
        doReturn(emp).when(service).getById(1L);
        doReturn(true).when(service).updateById(any());

        service.changeEmpStatus(1L, "RESIGNED");

        assertEquals("RESIGNED", emp.getEmpStatus());
        verify(service).updateById(emp);
    }

    // --- deleteEmployee ---

    @Test
    void deleteShouldThrowWhenEmployeeNotFound() {
        EmployeeServiceImpl service = makeService();
        doReturn(null).when(service).getById(99L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deleteEmployee(99L));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());
        verify(service, never()).removeById(any());
    }

    @Test
    void deleteShouldCallRemoveByIdWhenEmployeeExists() {
        EmployeeServiceImpl service = makeService();
        HrEmployee emp = validEmployee();
        emp.setId(1L);
        doReturn(emp).when(service).getById(1L);
        doReturn(true).when(service).removeById(1L);

        service.deleteEmployee(1L);

        verify(service).removeById(1L);
    }
}
