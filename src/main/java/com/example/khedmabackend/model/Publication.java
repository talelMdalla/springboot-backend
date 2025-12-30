package com.example.khedmabackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Import pour @JsonManagedReference
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publications")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    // ✅ Fix : Retire @JsonIgnore pour serialize author (nom/prénom/photo) – cycle broken par User's @JsonIgnore sur publications
    private User author; // Worker (type 'WORKER')

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl = "";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    @JsonManagedReference // Serialize likes (parent, break cycle Publication <-> Like)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    @JsonManagedReference // Serialize comments (parent, break cycle Publication <-> Comment)
    private List<Comment> comments = new ArrayList<>();

    @Transient // Derived, pas DB column
    private int likesCount = 0;

    @Transient // Derived, pas DB column
    private int commentsCount = 0;

    // Constructors
    public Publication() {}

    public Publication(User author, String content, String imageUrl) {
        this.author = author;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    // Getters/Setters pour counts (update en service)
    public int getLikesCount() { return likes.size(); }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getCommentsCount() { return comments.size(); }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}