package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // ✅ Fix : JPQL pour bookings par client email (JOIN users.email)
    @Query("SELECT b FROM Booking b JOIN b.client c WHERE c.email = :clientEmail ORDER BY b.date DESC")
    List<Booking> findByClientEmail(@Param("clientEmail") String clientEmail);

    // ✅ Fix : JPQL pour bookings par worker email (JOIN worker.user.email)
    @Query("SELECT b FROM Booking b JOIN b.worker w JOIN w.user u WHERE u.email = :workerEmail ORDER BY b.date DESC")
    List<Booking> findByWorkerEmail(@Param("workerEmail") String workerEmail);
}