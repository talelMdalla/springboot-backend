package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Message;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.repository.MessageRepository;
import com.example.khedmabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public Message sendMessage(Map<String, Object> request) {
        Long senderId = Long.parseLong(request.get("senderId").toString());
        Long receiverId = Long.parseLong(request.get("receiverId").toString());
        String content = (String) request.get("content");

        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message(sender, receiver, content);
        return messageRepository.save(message);
    }

    // Envoi message avec image (upload via stream, fix FileNotFound)
    public Message sendMessageWithImage(Long senderId, Long receiverId, String content, MultipartFile image) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        String imageUrl = "";
        if (image != null && !image.isEmpty()) {
            try {
                // Créé dir uploads si pas existant
                Path uploadDir = Paths.get("uploads/");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                // Nom unique
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                // Upload via inputStream (fix temp path Windows)
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                imageUrl = "/uploads/" + fileName; // Path relatif pour frontend
            } catch (IOException e) {
                throw new RuntimeException("Erreur upload image: " + e.getMessage());
            }
        }

        Message message = new Message(sender, receiver, content);
        message.setImageUrl(imageUrl); // Set imageUrl si uploadée
        message.setSentAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getConversation(Long userId, Long otherId) {
        return messageRepository.findConversationBetween(userId, otherId);
    }

    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findUnreadMessages(userId);
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    // Get all conversations (unique contacts)
    public List<Map<String, Object>> getAllConversations(Long userId) {
        return messageRepository.findAllConversations(userId);
    }
}