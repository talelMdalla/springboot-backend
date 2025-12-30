package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.Message;
import com.example.khedmabackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    private MessageService messageService;

    // ✅ Fix : Texte seulement (JSON)
    @PostMapping(path = "/text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sendTextMessage(@RequestBody Map<String, Object> request) {
        try {
            Message saved = messageService.sendMessage(request);
            Map<String, Object> response = Map.of("success", true, "message", saved);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ✅ Fix : Photo (multipart, texte optionnel)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> sendMessageWithImage(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "image", required = true) MultipartFile image) {
        try {
            Message saved = messageService.sendMessageWithImage(senderId, receiverId, content != null ? content : "", image);
            Map<String, Object> response = Map.of("success", true, "message", saved);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/conversation/{userId}/{otherId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long userId, @PathVariable Long otherId) {
        List<Message> conversation = messageService.getConversation(userId, otherId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Message>> getUnread(@PathVariable Long userId) {
        List<Message> unread = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(unread);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Get all conversations (unique contacts)
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAllConversations(@PathVariable Long userId) {
        List<Map<String, Object>> conversations = messageService.getAllConversations(userId);
        return ResponseEntity.ok(conversations);
    }
}