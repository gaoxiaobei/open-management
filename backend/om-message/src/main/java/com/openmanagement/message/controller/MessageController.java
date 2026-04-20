package com.openmanagement.message.controller;

import com.openmanagement.common.base.PageQuery;
import com.openmanagement.common.context.UserContext;
import com.openmanagement.common.result.PageResult;
import com.openmanagement.common.result.R;
import com.openmanagement.message.domain.entity.SysMessage;
import com.openmanagement.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/my")
    public R<PageResult<SysMessage>> myMessages(PageQuery pageQuery) {
        Long userId = UserContext.getUserId();
        return R.ok(messageService.myMessages(userId, pageQuery));
    }

    @PostMapping("/{id}/read")
    public R<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return R.ok();
    }

    @GetMapping("/unread-count")
    public R<Long> unreadCount() {
        Long userId = UserContext.getUserId();
        return R.ok(messageService.unreadCount(userId));
    }
}
