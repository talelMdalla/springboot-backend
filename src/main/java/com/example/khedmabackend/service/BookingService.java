package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Booking;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.repository.BookingRepository;
import com.example.khedmabackend.repository.UserRepository;
import com.example.khedmabackend.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    public Booking saveBooking(Booking booking) {
        if (booking.getClientId() != null) {
            Optional<User> clientOpt = userRepository.findById(booking.getClientId());
            clientOpt.ifPresent(booking::setClient);
        }
        if (booking.getWorkerId() != null) {
            Optional<Worker> workerOpt = workerRepository.findById(booking.getWorkerId());
            workerOpt.ifPresent(booking::setWorker);
        }
        booking.setDate(LocalDateTime.now());
        booking.setStatus("PENDING"); // String uppercase
        return bookingRepository.save(booking);
    }

    public List<Booking> findByClientEmail(String clientEmail) {
        return bookingRepository.findByClientEmail(clientEmail);
    }

    // Liste bookings pour worker par son user.email (match type 'WORKER')
    public List<Booking> findByWorkerEmail(String workerEmail) {
        return bookingRepository.findByWorkerEmail(workerEmail);
    }

    // Accepter résa (update status "ACCEPTEE")
    public Booking acceptBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("ACCEPTEE"); // ✅ Nouveau : "Acceptée"
        return bookingRepository.save(booking);
    }

    // Rejeter résa (update status "REFUSEE")
    public Booking rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("REFUSEE"); // ✅ Nouveau : "Refusée"
        return bookingRepository.save(booking);
    }
}