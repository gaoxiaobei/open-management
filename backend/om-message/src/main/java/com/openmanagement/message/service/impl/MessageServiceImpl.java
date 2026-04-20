package com.openmanagement.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.message.domain.entity.SysMessage;
import com.openmanagement.message.mapper.MessageMapper;
import com.openmanagement.message.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, SysMessage> implements MessageService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(Long receiverId, String title, String content, String msgType) {
        if (receiverId == null) {
            throw new BusinessException("receiverId must not be null");
        }
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("title must not be blank");
        }
        if (!StringUtils.hasText(msgType)) {
            throw new BusinessException("msgType must not be blank");
        }
        Long senderId = UserContext.getUserId();
        if (senderId == null) {
            throw new BusinessException("Current user context is required to send a message");
        }
        SysMessage message = new SysMessage();
        message.setReceiverId(receiverId);
        message.setSenderId(senderId);
        message.setTitle(title);
        message.setContent(content);
        message.setMsgType(msgType);
        message.setIsRead(0);
        save(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long messageId) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException("Current user context is required to mark a message as read");
        }
        boolean updated = update(new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getId, messageId)
                .eq(SysMessage::getReceiverId, currentUserId)
                .set(SysMessage::getIsRead, 1)
                .set(SysMessage::getReadTime, LocalDateTime.now()));
        if (!updated) {
            throw new BusinessException("Message not found or not owned by current user");
        }
    }

    @Override
    public PageResult<SysMessage> myMessages(Long userId, PageQuery pageQuery) {
        Page<SysMessage> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<SysMessage> result = page(page, new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, userId)
                .orderByDesc(SysMessage::getCreatedAt));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public long unreadCount(Long userId) {
        return count(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, userId)
                .eq(SysMessage::getIsRead, 0));
    }
}
