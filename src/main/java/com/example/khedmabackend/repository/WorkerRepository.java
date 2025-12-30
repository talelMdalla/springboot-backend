package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    @Query("SELECT w FROM Worker w WHERE w.category = :category AND w.isAvailable = :available ORDER BY w.rating DESC")
    List<Worker> findByCategoryAndAvailable(@Param("category") String category, @Param("available") boolean available);

    @Query("SELECT w FROM Worker w JOIN FETCH w.user u WHERE w.category = :category ORDER BY w.rating DESC") // ✅ Fix : JOIN FETCH pour inclure profileImage en liste par catégorie
    List<Worker> findAllByCategoryWithUser(@Param("category") String category);

    @Query("SELECT w FROM Worker w JOIN FETCH w.user u WHERE w.category = :category AND w.governorate = :governorate ORDER BY w.rating DESC") // ✅ Fix : JOIN FETCH pour search
    List<Worker> findByCategoryAndGovernorateWithUser(@Param("category") String category, @Param("governorate") String governorate);

    // ✅ Fix : Query complète pour findByUserId
    @Query("SELECT w FROM Worker w WHERE w.user.id = :userId")
    Optional<Worker> findByUserId(@Param("userId") Long userId);
}