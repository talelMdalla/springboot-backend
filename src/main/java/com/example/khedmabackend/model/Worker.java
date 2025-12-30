package com.example.khedmabackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List; // ✅ Fix : Import List pour setSkills (pas warning)

@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // ✅ Fix : Ajoute userId pour fallback WorkerController (ID user)
    @Column(name = "user_id", insertable = false, updatable = false) // Ne s'insert/update pas (géré par @JoinColumn)
    private Long userId;

    private String category;
    private String governorate;
    private String description;
    private Double pricePerHour = 0.0;
    private Double rating = 0.0;
    private Integer reviewCount = 0;
    private Boolean isAvailable = true;

    @ElementCollection
    private List<String> skills;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    // Constructors
    public Worker() {}

    public Worker(User user, String category, String governorate) {
        this.user = user;
        this.category = category;
        this.governorate = governorate;
    }

    // Getters/Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ✅ Fix : Getter/setter pour userId (fallback Controller)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    } // ✅ Fix : Pas warning (import List)

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}