package com.UMarket.backend.controller;

import com.UMarket.backend.model.Message;
import com.UMarket.backend.service.MessageService;
import com.UMarket.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // POST /api/messages?recipientId=2&listingId=5
    @PostMapping
    public ResponseEntity<?> send(
            @RequestBody Message message,
            @RequestParam Long recipientId,
            @RequestParam Long listingId) {
        try {
            Long senderId = getAuthenticatedId();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(messageService.send(message, senderId, recipientId, listingId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/messages/inbox
    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getInbox() {
        Long userId = getAuthenticatedId();
        return ResponseEntity.ok(messageService.getInbox(userId));
    }

    // GET /api/messages/unread
    @GetMapping("/unread")
    public ResponseEntity<Long> countUnread() {
        Long userId = getAuthenticatedId();
        return ResponseEntity.ok(messageService.countUnread(userId));
    }

    // PUT /api/messages/1/read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            Long userId = getAuthenticatedId();
            return ResponseEntity.ok(messageService.markAsRead(id, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Long getAuthenticatedId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }
}
