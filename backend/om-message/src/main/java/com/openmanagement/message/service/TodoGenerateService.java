package com.openmanagement.message.service;

import java.util.Collection;

public interface TodoGenerateService {

    void generateTodo(Long receiverId, String title, String content, String bizType, Long bizId);

    void generateTodos(Collection<Long> receiverIds, String title, String content, String bizType, Long bizId);

    int completeTodo(Long receiverId, String bizType, Long bizId);

    int completeTodos(Collection<Long> receiverIds, String bizType, Long bizId);
}
