package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Publication;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Comment;
import com.example.khedmabackend.model.Like;
import com.example.khedmabackend.repository.PublicationRepository;
import com.example.khedmabackend.repository.UserRepository;
import com.example.khedmabackend.repository.CommentRepository;
import com.example.khedmabackend.repository.LikeRepository;
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
import java.util.Optional;
import java.util.UUID; // ✅ Fix : Import UUID pour randomUUID()

@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Publication createPublication(Long authorId, String content, MultipartFile image) {
        User author = userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("Author not found"));

        String imageUrl = "";
        if (image != null && !image.isEmpty()) {
            try {
                Path uploadDir = Paths.get("uploads/");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename(); // ✅ Fix : UUID importé
                Path filePath = uploadDir.resolve(fileName);
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                imageUrl = "/uploads/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("Erreur upload image: " + e.getMessage());
            }
        }

        Publication publication = new Publication();
        publication.setAuthor(author);
        publication.setContent(content);
        publication.setImageUrl(imageUrl);
        publication.setCreatedAt(LocalDateTime.now());
        publication.setLikesCount(0);
        publication.setCommentsCount(0);
        return publicationRepository.save(publication);
    }

    public List<Publication> getAllPublications() {
        return publicationRepository.findAllByOrderByCreatedAtDesc(); // Récentes → anciennes
    }

    public List<Publication> getPublicationsByType(String type) {
        return publicationRepository.findByAuthorType(type);
    }

    public Publication findById(Long id) {
        return publicationRepository.findById(id).orElse(null);
    }

    // Like (toggle)
    public Publication likePublication(Long publicationId, Long userId) {
        Publication publication = publicationRepository.findById(publicationId).orElseThrow(() -> new RuntimeException("Publication not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already liked
        Optional<Like> existingLike = likeRepository.findByPublicationAndUser(publication, user);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            publication.setLikesCount(publication.getLikesCount() - 1);
        } else {
            Like like = new Like(publication, user);
            likeRepository.save(like);
            publication.setLikesCount(publication.getLikesCount() + 1);
        }
        return publicationRepository.save(publication);
    }

    // Ajouter commentaire
    public Publication addComment(Long publicationId, Long userId, String commentText) {
        Publication publication = publicationRepository.findById(publicationId).orElseThrow(() -> new RuntimeException("Publication not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setPublication(publication);
        comment.setUser(user);
        comment.setText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        publication.setCommentsCount(publication.getCommentsCount() + 1);
        return publicationRepository.save(publication);
    }

    // ✅ Nouveau : Lister commentaires pour publication (avec JOIN FETCH user pour nom/photo)
    public List<Comment> getComments(Long publicationId) {
        return commentRepository.findByPublicationIdWithUser(publicationId);
    }
}