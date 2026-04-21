package com.openmanagement.message.service;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.message.domain.entity.SysMessage;

public interface MessageService {

    void send(Long receiverId, String title, String content, String msgType);

    void send(Long receiverId, String title, String content, String msgType, String bizType, Long bizId);

    void markRead(Long messageId);

    PageResult<SysMessage> myMessages(Long userId, PageQuery pageQuery);

    long unreadCount(Long userId);
}
