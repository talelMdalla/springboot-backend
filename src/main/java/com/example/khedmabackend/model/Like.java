package com.example.khedmabackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // ✅ Fix : Import pour @JsonBackReference
import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    @JsonBackReference // ✅ Fix : Break cycle Like -> Publication (parent ignored in serialization)
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Like() {}

    public Like(Publication publication, User user) {
        this.publication = publication;
        this.user = user;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}