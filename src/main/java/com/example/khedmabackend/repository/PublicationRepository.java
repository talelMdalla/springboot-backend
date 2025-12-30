package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    // Récentes → anciennes
    List<Publication> findAllByOrderByCreatedAtDesc();

    // Par type author (CLIENT/WORKER)
    @Query("SELECT p FROM Publication p JOIN p.author u WHERE u.type = :type ORDER BY p.createdAt DESC")
    List<Publication> findByAuthorType(@Param("type") String type);
}