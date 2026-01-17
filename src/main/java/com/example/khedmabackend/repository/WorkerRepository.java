package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    // ✅ Fix : Query pour workers par catégorie + JOIN fetch user (isAvailable au lieu de available)
    @Query("SELECT w FROM Worker w LEFT JOIN FETCH w.user u WHERE w.category = :category AND w.isAvailable = true ORDER BY w.averageRating DESC")
    List<Worker> findAllByCategoryWithUser(@Param("category") String category);

    // ✅ Fix : Query pour search catégorie + governorate + JOIN fetch user (isAvailable)
    @Query("SELECT w FROM Worker w LEFT JOIN FETCH w.user u WHERE w.category = :category AND w.governorate = :governorate AND w.isAvailable = true ORDER BY w.averageRating DESC")
    List<Worker> findByCategoryAndGovernorateWithUser(@Param("category") String category, @Param("governorate") String governorate);

    // ✅ Optionnel : Workers disponibles par catégorie (sans JOIN si perf issue)
    List<Worker> findByCategoryAndIsAvailableTrue(String category);
}