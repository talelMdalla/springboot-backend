package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Review;
import com.example.khedmabackend.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // ✅ Query : Récupérer avis par worker (avec client info pour nom/photo)
    @Query("SELECT r FROM Review r JOIN FETCH r.client c WHERE r.worker.id = :workerId ORDER BY r.createdAt DESC")
    List<Review> findByWorkerIdWithClient(@Param("workerId") Long workerId);

    // ✅ Query : Compter nombre d'avis par worker
    @Query("SELECT COUNT(r) FROM Review r WHERE r.worker.id = :workerId")
    long countByWorkerId(@Param("workerId") Long workerId);

    // ✅ Query : Moyenne étoiles par worker
    @Query("SELECT AVG(r.stars) FROM Review r WHERE r.worker.id = :workerId")
    Double averageStarsByWorkerId(@Param("workerId") Long workerId);
}