package com.openmanagement.message.service.impl;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.message.domain.entity.SysMessage;
import com.openmanagement.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    @Override
    public void send(Long receiverId, String title, String content, String msgType) {
        // TODO: persist SysMessage, publish to RabbitMQ for async delivery
    }

    @Override
    public void markRead(Long messageId) {
        // TODO: update isRead and readTime for the given messageId
    }

    @Override
    public PageResult<SysMessage> myMessages(Long userId, PageQuery pageQuery) {
        // TODO: query messages for userId with pagination
        return PageResult.of(0L, Collections.emptyList());
    }
}
