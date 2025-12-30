package com.example.khedmabackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // ✅ Fix : Import pour @JsonBackReference
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @JsonBackReference // ✅ Fix : Break cycle Comment -> Publication (parent ignored in serialization)
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Comment() {}

    public Comment(Publication publication, User user, String text) {
        this.publication = publication;
        this.user = user;
        this.text = text;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}