package com.openmanagement.message.service.impl;

import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.message.domain.entity.SysMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class MessageServiceImplTest {

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void sendShouldAllowSystemGeneratedMessages() {
        MessageServiceImpl service = spy(new MessageServiceImpl());
        ArgumentCaptor<SysMessage> messageCaptor = ArgumentCaptor.forClass(SysMessage.class);

        doReturn(true).when(service).save(any(SysMessage.class));

        service.send(2L, "审批待办", "请处理请假申请", "TODO", "LEAVE_APPLY", 1001L);

        verify(service).save(messageCaptor.capture());
        SysMessage message = messageCaptor.getValue();
        assertEquals(2L, message.getReceiverId());
        assertNull(message.getSenderId());
        assertEquals("TODO", message.getMsgType());
        assertEquals("LEAVE_APPLY", message.getBizType());
        assertEquals(1001L, message.getBizId());
        assertEquals(0, message.getIsRead());
    }

    @Test
    void sendShouldUseCurrentUserAsSenderWhenPresent() {
        MessageServiceImpl service = spy(new MessageServiceImpl());
        ArgumentCaptor<SysMessage> messageCaptor = ArgumentCaptor.forClass(SysMessage.class);
        UserContext.setUserId(9L);

        doReturn(true).when(service).save(any(SysMessage.class));

        service.send(2L, "系统通知", "测试内容", "NOTICE");

        verify(service).save(messageCaptor.capture());
        assertEquals(9L, messageCaptor.getValue().getSenderId());
    }

    @Test
    void sendShouldRejectPartialBusinessLinkage() {
        MessageServiceImpl service = spy(new MessageServiceImpl());

        assertThrows(BusinessException.class,
                () -> service.send(2L, "审批待办", "请处理请假申请", "TODO", "LEAVE_APPLY", null));
        verify(service, never()).save(any(SysMessage.class));
    }
}
