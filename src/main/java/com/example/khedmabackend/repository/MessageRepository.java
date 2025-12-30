package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :otherId) OR (m.sender.id = :otherId AND m.receiver.id = :userId) ORDER BY m.sentAt ASC")
    List<Message> findConversationBetween(@Param("userId") Long userId, @Param("otherId") Long otherId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false ORDER BY m.sentAt ASC")
    List<Message> findUnreadMessages(@Param("userId") Long userId);

    // ✅ Fix : Query native corrigée (unique other_id, last message, unread count – subquery sans alias conflict)
    @Query(value = "WITH interactions AS (" +
            "  SELECT DISTINCT " +
            "    CASE WHEN sender_id = :userId THEN receiver_id ELSE sender_id END as other_id, " +
            "    u.nom, u.prenom, u.profile_image " +
            "  FROM messages m " +
            "  JOIN users u ON u.id = CASE WHEN sender_id = :userId THEN receiver_id ELSE sender_id END " +
            "  WHERE sender_id = :userId OR receiver_id = :userId " +
            ") " +
            "SELECT i.other_id, i.nom, i.prenom, i.profile_image, " +
            "  (SELECT m.content FROM messages m WHERE (m.sender_id = :userId AND m.receiver_id = i.other_id) OR (m.sender_id = i.other_id AND m.receiver_id = :userId) ORDER BY m.sent_at DESC LIMIT 1) as last_content, " +
            "  (SELECT m.sent_at FROM messages m WHERE (m.sender_id = :userId AND m.receiver_id = i.other_id) OR (m.sender_id = i.other_id AND m.receiver_id = :userId) ORDER BY m.sent_at DESC LIMIT 1) as sent_at, " +
            "  (SELECT COUNT(*) FROM messages m WHERE m.receiver_id = :userId AND m.is_read = false AND m.sender_id = i.other_id) as unread_count " +
            "FROM interactions i " +
            "ORDER BY sent_at DESC", nativeQuery = true)
    List<Map<String, Object>> findAllConversations(@Param("userId") Long userId);
}