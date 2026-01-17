package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Booking;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.repository.BookingRepository;
import com.example.khedmabackend.repository.UserRepository;
import com.example.khedmabackend.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Transactional
    public Booking saveBooking(Booking booking) {
        logger.info("Save booking: clientId={}, workerId={}", booking.getClientId(), booking.getWorkerId());
        if (booking.getClientId() != null) {
            Optional<User> clientOpt = userRepository.findById(booking.getClientId());
            clientOpt.ifPresent(booking::setClient);
            logger.debug("Client set: {}", booking.getClient() != null ? booking.getClient().getEmail() : "null");
        }
        if (booking.getWorkerId() != null) {
            Optional<Worker> workerOpt = workerRepository.findById(booking.getWorkerId());
            workerOpt.ifPresent(booking::setWorker);
            logger.debug("Worker set: {}", booking.getWorker() != null ? booking.getWorker().getUser().getEmail() : "null");
        }
        booking.setDate(LocalDateTime.now());
        booking.setStatus("PENDING"); // String uppercase
        Booking saved = bookingRepository.save(booking);
        logger.info("Booking saved ID={}", saved.getId());
        return saved;
    }

    // ✅ Fix : Liste bookings par client email (JPQL avec JOIN, logs count)
    public List<Booking> findByClientEmail(String clientEmail) {
        logger.info("Query bookings for client email: {}", clientEmail);
        List<Booking> bookings = bookingRepository.findByClientEmail(clientEmail);
        logger.info("Found {} bookings for client {}", bookings.size(), clientEmail);
        return bookings;
    }

    // ✅ Fix : Liste bookings pour worker par user.email (JPQL avec JOIN, logs count)
    public List<Booking> findByWorkerEmail(String workerEmail) {
        logger.info("Query bookings for worker email: {}", workerEmail);
        List<Booking> bookings = bookingRepository.findByWorkerEmail(workerEmail);
        logger.info("Found {} bookings for worker {}", bookings.size(), workerEmail);
        return bookings;
    }

    // Accepter résa (update status "ACCEPTEE")
    @Transactional
    public Booking acceptBooking(Long bookingId) {
        logger.info("Accept booking ID={}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.setStatus("ACCEPTEE"); // ✅ Nouveau : "Acceptée"
        Booking updated = bookingRepository.save(booking);
        logger.info("Booking {} accepted", updated.getId());
        return updated;
    }

    // Rejeter résa (update status "REFUSEE")
    @Transactional
    public Booking rejectBooking(Long bookingId) {
        logger.info("Reject booking ID={}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.setStatus("REFUSEE"); // ✅ Nouveau : "Refusée"
        Booking updated = bookingRepository.save(booking);
        logger.info("Booking {} rejected", updated.getId());
        return updated;
    }

    // Assume service existe ; ajoute getBookingsByClient si besoin
    public List<Booking> getBookingsByClient(String email) {
        return findByClientEmail(email); // Query simple, pas fetch worker.reviews
    }

    public List<Booking> getBookingsByWorker(String email) {
        return findByWorkerEmail(email); // Query simple, logs dans findByWorkerEmail
    }
}