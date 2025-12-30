package com.example.khedmabackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // ✅ Fix : Import pour @JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty; // ✅ Import pour ACCESS (WRITE_ONLY)
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // ✅ Fix : WRITE_ONLY (incoming OK, outgoing masqué pour sécurité)
    private String password;

    private String type;
    private String dateNaissance;
    private String adresse;
    private String phone;
    private String category;
    private String description;
    private String governorate;

    @Column(name = "profile_image") // ✅ Photo profil
    private String profileImage;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL) // ✅ Fix : Back-reference to Publication
    @JsonIgnore // ✅ Fix : Ignore publications en serialization (break cycle User -> Publications -> User -> ...)
    private List<Publication> publications = new ArrayList<>(); // ✅ Nouveau : List publications (si pas existant)

    // Constructors
    public User() {}

    public User(String nom, String prenom, String email, String password, String type, String dateNaissance, String adresse, String phone, String category, String description, String governorate) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.type = type;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.phone = phone;
        this.category = category;
        this.description = description;
        this.governorate = governorate;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGovernorate() { return governorate; }
    public void setGovernorate(String governorate) { this.governorate = governorate; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    // ✅ Nouveau : Getters/Setters pour publications (ignoré en JSON)
    public List<Publication> getPublications() { return publications; }
    public void setPublications(List<Publication> publications) { this.publications = publications; }
}