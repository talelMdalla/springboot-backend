package com.example.khedmabackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker; // Worker commenté (référence)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client; // Client qui commente (référence)

    @Column(nullable = false)
    private int stars; // Étoiles 0-5

    @Column(columnDefinition = "TEXT")
    private String comment; // Texte commentaire

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Date création

    // Constructeurs
    public Review() {}

    public Review(Worker worker, User client, int stars, String comment) {
        this.worker = worker;
        this.client = client;
        this.stars = stars;
        this.comment = comment;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}