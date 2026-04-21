package com.openmanagement.message.service.impl;

import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.message.constant.MessageTypeConstants;
import com.openmanagement.message.mapper.MessageMapper;
import com.openmanagement.message.service.MessageService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TodoGenerateServiceImplTest {

    @Test
    void generateTodoShouldDelegateToMessageServiceWithTodoType() {
        MessageService messageService = mock(MessageService.class);
        MessageMapper messageMapper = mock(MessageMapper.class);
        TodoGenerateServiceImpl service = new TodoGenerateServiceImpl(messageService, messageMapper);

        service.generateTodo(3L, "待审批", "请处理申请单", "LEAVE_APPLY", 2002L);

        verify(messageService).send(3L, "待审批", "请处理申请单", MessageTypeConstants.TODO, "LEAVE_APPLY", 2002L);
    }

    @Test
    void completeTodosShouldReturnZeroForEmptyReceivers() {
        MessageService messageService = mock(MessageService.class);
        MessageMapper messageMapper = mock(MessageMapper.class);
        TodoGenerateServiceImpl service = new TodoGenerateServiceImpl(messageService, messageMapper);

        assertEquals(0, service.completeTodos(List.of(), "LEAVE_APPLY", 2002L));
        verifyNoInteractions(messageService, messageMapper);
    }

    @Test
    void completeTodoShouldReturnUpdatedCount() {
        MessageService messageService = mock(MessageService.class);
        MessageMapper messageMapper = mock(MessageMapper.class);
        TodoGenerateServiceImpl service = new TodoGenerateServiceImpl(messageService, messageMapper);

        doReturn(2).when(messageMapper).update(isNull(), any());

        assertEquals(2, service.completeTodo(3L, "LEAVE_APPLY", 2002L));
    }

    @Test
    void generateTodoShouldRejectMissingBusinessLink() {
        MessageService messageService = mock(MessageService.class);
        MessageMapper messageMapper = mock(MessageMapper.class);
        TodoGenerateServiceImpl service = new TodoGenerateServiceImpl(messageService, messageMapper);

        assertThrows(BusinessException.class, () -> service.generateTodo(3L, "待审批", "请处理申请单", "", 2002L));
        verifyNoInteractions(messageService, messageMapper);
    }

    @Test
    void generateTodoShouldRejectNullBusinessId() {
        MessageService messageService = mock(MessageService.class);
        MessageMapper messageMapper = mock(MessageMapper.class);
        TodoGenerateServiceImpl service = new TodoGenerateServiceImpl(messageService, messageMapper);

        assertThrows(BusinessException.class,
                () -> service.generateTodo(3L, "待审批", "请处理申请单", "LEAVE_APPLY", null));
        verifyNoInteractions(messageService, messageMapper);
    }
}
