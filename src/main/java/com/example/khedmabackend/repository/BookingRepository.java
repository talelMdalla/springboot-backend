package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // ✅ Fix : Native SQL pour bookings par client email (join direct sur users.email)
    @Query(value = "SELECT b.* FROM bookings b JOIN users c ON b.client_id = c.id WHERE c.email = :clientEmail", nativeQuery = true)
    List<Booking> findByClientEmail(@Param("clientEmail") String clientEmail);

    // ✅ Fix : Native SQL pour bookings par worker email (join worker.user.email)
    @Query(value = "SELECT b.* FROM bookings b JOIN workers w ON b.worker_id = w.id JOIN users u ON w.user_id = u.id WHERE u.email = :workerEmail", nativeQuery = true)
    List<Booking> findByWorkerEmail(@Param("workerEmail") String workerEmail);
}