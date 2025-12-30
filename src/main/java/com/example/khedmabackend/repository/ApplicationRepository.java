package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByWorkerIdAndPublicationId(Long workerId, Long publicationId); // Évite doublons
}