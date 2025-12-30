package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Comment;
import com.example.khedmabackend.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // ✅ Nouveau : Custom query pour fetch comments avec user (nom/prénom/profileImage) pour JSON complet
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user WHERE c.publication.id = :publicationId ORDER BY c.createdAt ASC")
    List<Comment> findByPublicationIdWithUser(@Param("publicationId") Long publicationId);
}