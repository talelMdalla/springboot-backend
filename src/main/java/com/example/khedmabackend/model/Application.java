package com.example.khedmabackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime; // ✅ Fix : Import manquant pour LocalDateTime

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker; // Worker qui postule

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @Column(name = "applied_at")
    private String appliedAt = LocalDateTime.now().toString(); // Timestamp simple (string pour JSON)

    // Constructors
    public Application() {}

    public Application(User worker, Publication publication) {
        this.worker = worker;
        this.publication = publication;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }

    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }
}