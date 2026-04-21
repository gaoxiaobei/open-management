package com.openmanagement.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.openmanagement.common.exception.BusinessException;
import com.openmanagement.message.constant.MessageTypeConstants;
import com.openmanagement.message.domain.entity.SysMessage;
import com.openmanagement.message.mapper.MessageMapper;
import com.openmanagement.message.service.MessageService;
import com.openmanagement.message.service.TodoGenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodoGenerateServiceImpl implements TodoGenerateService {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateTodo(Long receiverId, String title, String content, String bizType, Long bizId) {
        validateReceiver(receiverId);
        validateBizLink(bizType, bizId);
        messageService.send(receiverId, title, content, MessageTypeConstants.TODO, bizType, bizId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateTodos(Collection<Long> receiverIds, String title, String content, String bizType, Long bizId) {
        if (CollectionUtils.isEmpty(receiverIds)) {
            return;
        }
        validateBizLink(bizType, bizId);
        normalizeReceiverIds(receiverIds).forEach(receiverId ->
                messageService.send(receiverId, title, content, MessageTypeConstants.TODO, bizType, bizId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeTodo(Long receiverId, String bizType, Long bizId) {
        validateReceiver(receiverId);
        validateBizLink(bizType, bizId);
        return messageMapper.update(null, new UpdateWrapper<SysMessage>()
                .eq("receiver_id", receiverId)
                .eq("msg_type", MessageTypeConstants.TODO)
                .eq("biz_type", bizType)
                .eq("biz_id", bizId)
                .eq("is_read", 0)
                .set("is_read", 1)
                .set("read_time", LocalDateTime.now()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeTodos(Collection<Long> receiverIds, String bizType, Long bizId) {
        if (CollectionUtils.isEmpty(receiverIds)) {
            return 0;
        }
        validateBizLink(bizType, bizId);
        Set<Long> normalizedReceiverIds = normalizeReceiverIds(receiverIds);
        return messageMapper.update(null, new UpdateWrapper<SysMessage>()
                .in("receiver_id", normalizedReceiverIds)
                .eq("msg_type", MessageTypeConstants.TODO)
                .eq("biz_type", bizType)
                .eq("biz_id", bizId)
                .eq("is_read", 0)
                .set("is_read", 1)
                .set("read_time", LocalDateTime.now()));
    }

    private void validateReceiver(Long receiverId) {
        if (receiverId == null) {
            throw new BusinessException("receiverId must not be null");
        }
    }

    private void validateBizLink(String bizType, Long bizId) {
        if (!StringUtils.hasText(bizType)) {
            throw new BusinessException("bizType must not be blank");
        }
        if (bizId == null) {
            throw new BusinessException("bizId must not be null");
        }
    }

    private Set<Long> normalizeReceiverIds(Collection<Long> receiverIds) {
        Set<Long> normalizedReceiverIds = new LinkedHashSet<>();
        for (Long receiverId : receiverIds) {
            if (receiverId != null) {
                normalizedReceiverIds.add(receiverId);
            }
        }
        if (normalizedReceiverIds.isEmpty()) {
            throw new BusinessException("receiverIds must contain at least one non-null receiverId");
        }
        return normalizedReceiverIds;
    }
}
