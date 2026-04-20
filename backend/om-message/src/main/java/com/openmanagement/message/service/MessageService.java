package com.openmanagement.message.service;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.message.domain.entity.SysMessage;

public interface MessageService {

    void send(Long receiverId, String title, String content, String msgType);

    void markRead(Long messageId);

    PageResult<SysMessage> myMessages(Long userId, PageQuery pageQuery);
}
