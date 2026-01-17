package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.Booking;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.service.BookingService;
import com.example.khedmabackend.service.UserService;
import com.example.khedmabackend.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*") // ✅ Global CORS (couvre POST/GET/OPTIONS)
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService; // Pour fetch client

    @Autowired
    private WorkerService workerService; // Pour fetch worker

    // POST créer booking (CORS pour POST/OPTIONS)
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Booking booking) {
        System.out.println("Debug create booking: Request received! ClientId = " + booking.getClientId()); // Log pour confirmer
        try {
            Booking savedBooking = bookingService.saveBooking(booking);
            Map<String, Object> flatBooking = mapBookingToResponse(savedBooking); // ✅ Flat pour éviter circular
            return ResponseEntity.ok(Map.of("success", true, "booking", flatBooking));
        } catch (Exception e) {
            Map<String, Object> error = Map.of("success", false, "message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET bookings par client email (CORS pour GET/OPTIONS + decode + flat response)
    @GetMapping("/client/{clientEmail}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Map<String, Object>>> getBookingsByClient(@PathVariable String clientEmail) {
        // Decode email
        String decodedEmail = URLDecoder.decode(clientEmail, StandardCharsets.UTF_8);
        System.out.println("Debug bookings client: Encoded = " + clientEmail + ", Decoded = " + decodedEmail); // Log
        List<Booking> bookings = bookingService.findByClientEmail(decodedEmail);
        System.out.println("Debug bookings client: Found " + bookings.size() + " bookings"); // Log
        List<Map<String, Object>> flatBookings = bookings.stream().map(this::mapBookingToResponse).toList(); // ✅ Flat pour éviter circular
        return ResponseEntity.ok(flatBookings);
    }

    // GET bookings par worker email (même fix + flat)
    @GetMapping("/worker/email/{workerEmail}")
    @PreAuthorize("hasRole('WORKER') or authentication.name == #workerEmail")
    public ResponseEntity<List<Map<String, Object>>> getBookingsByWorkerEmail(@PathVariable String workerEmail) {
        String decodedEmail = URLDecoder.decode(workerEmail, StandardCharsets.UTF_8);
        System.out.println("Debug bookings worker: Encoded = " + workerEmail + ", Decoded = " + decodedEmail); // Log
        List<Booking> bookings = bookingService.findByWorkerEmail(decodedEmail);
        List<Map<String, Object>> flatBookings = bookings.stream().map(this::mapBookingToResponse).toList(); // ✅ Flat
        return ResponseEntity.ok(flatBookings);
    }

    // Accepter résa
    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Map<String, Object>> acceptBooking(@PathVariable Long id) {
        try {
            Booking updated = bookingService.acceptBooking(id);
            Map<String, Object> flatBooking = mapBookingToResponse(updated); // ✅ Flat
            return ResponseEntity.ok(Map.of("success", true, "booking", flatBooking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Rejeter résa
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Map<String, Object>> rejectBooking(@PathVariable Long id) {
        try {
            Booking updated = bookingService.rejectBooking(id);
            Map<String, Object> flatBooking = mapBookingToResponse(updated); // ✅ Flat
            return ResponseEntity.ok(Map.of("success", true, "booking", flatBooking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ✅ Nouveau : Map flat pour Booking (pas nesting worker/reviews pour JSON court)
    private Map<String, Object> mapBookingToResponse(Booking booking) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", booking.getId());
        response.put("status", booking.getStatus());

        // Flat client (seulement nom/prénom/photo)
        User client = booking.getClient();
        response.put("clientNom", client.getNom());
        response.put("clientPrenom", client.getPrenom());
        response.put("clientProfileImage", client.getProfileImage());

        // Flat worker (seulement nom/prénom/catégorie/photo, pas reviews)
        Worker worker = booking.getWorker();
        response.put("workerNom", worker.getUser().getNom());
        response.put("workerPrenom", worker.getUser().getPrenom());
        response.put("workerCategory", worker.getCategory());
        response.put("workerProfileImage", worker.getUser().getProfileImage());
        response.put("workerRating", worker.getAverageRating()); // Moyen étoiles
        response.put("workerReviewCount", worker.getReviewCount());

        response.put("date", booking.getDate().toString());
        response.put("time", booking.getTime());
        response.put("location", booking.getLocation());
        response.put("price", booking.getPrice());
        response.put("service", booking.getService());

        return response;
    }
}