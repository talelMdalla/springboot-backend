package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.Booking;
import com.example.khedmabackend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    // POST créer booking (CORS pour POST/OPTIONS)
    @PostMapping
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.OPTIONS}) // ✅ Fix : CORS pour POST + preflight
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        System.out.println("Debug create booking: Request received! ClientId = " + booking.getClientId()); // Log pour confirmer
        Booking savedBooking = bookingService.saveBooking(booking);
        return ResponseEntity.ok(savedBooking);
    }

    // GET bookings par client email (CORS pour GET/OPTIONS + decode)
    @GetMapping("/client/{clientEmail}")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS}) // ✅ Fix : CORS pour GET + preflight
    @PreAuthorize("hasRole('CLIENT') or authentication.name == #clientEmail")
    public ResponseEntity<List<Booking>> getBookingsByClient(@PathVariable String clientEmail) {
        // Decode email
        String decodedEmail = URLDecoder.decode(clientEmail, StandardCharsets.UTF_8);
        System.out.println("Debug bookings client: Encoded = " + clientEmail + ", Decoded = " + decodedEmail); // Log
        List<Booking> bookings = bookingService.findByClientEmail(decodedEmail);
        System.out.println("Debug bookings client: Found " + bookings.size() + " bookings");
        return ResponseEntity.ok(bookings);
    }

    // GET bookings par worker email (même fix)
    @GetMapping("/worker/email/{workerEmail}")
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
    @PreAuthorize("hasRole('WORKER') or authentication.name == #workerEmail")
    public ResponseEntity<List<Booking>> getBookingsByWorkerEmail(@PathVariable String workerEmail) {
        String decodedEmail = URLDecoder.decode(workerEmail, StandardCharsets.UTF_8);
        System.out.println("Debug bookings worker: Encoded = " + workerEmail + ", Decoded = " + decodedEmail);
        List<Booking> bookings = bookingService.findByWorkerEmail(decodedEmail);
        return ResponseEntity.ok(bookings);
    }

    // Accepter résa
    @PutMapping("/{id}/accept")
    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Map<String, Object>> acceptBooking(@PathVariable Long id) {
        try {
            Booking updated = bookingService.acceptBooking(id);
            return ResponseEntity.ok(Map.of("success", true, "booking", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Rejeter résa
    @PutMapping("/{id}/reject")
    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Map<String, Object>> rejectBooking(@PathVariable Long id) {
        try {
            Booking updated = bookingService.rejectBooking(id);
            return ResponseEntity.ok(Map.of("success", true, "booking", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}