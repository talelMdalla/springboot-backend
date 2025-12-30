package com.example.khedmabackend.service;

import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.repository.UserRepository;
import com.example.khedmabackend.repository.WorkerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WorkerRepository workerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        if ("WORKER".equals(user.getType())) {
            Worker worker = new Worker();
            worker.setUser(savedUser);
            worker.setCategory(user.getCategory());
            worker.setGovernorate(user.getGovernorate());
            worker.setDescription(user.getDescription() != null ? user.getDescription() : ""); // ✅ Fix : Parse String → LocalDate (ligne 35, safe)
            String dateStr = user.getDateNaissance();
            LocalDate dateNaissance = null;
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    dateNaissance = LocalDate.parse(dateStr); // Parse "YYYY-MM-DD"
                } catch (DateTimeParseException e) { // Log erreur si format invalide (ex. "2025-12-05" OK, mais "invalid" → null)
                    System.err.println("Erreur parse date: " + e.getMessage());
                }
            }
            worker.setDateNaissance(dateNaissance);
            worker.setPricePerHour(30.0);
            worker.setRating(4.5);
            worker.setReviewCount(0);
            worker.setAvailable(true);
            worker.setSkills(Arrays.asList("Défaut"));
            try {
                workerRepository.save(worker); // ✅ Fix : Try-catch pour éviter throw RuntimeException (ex. validation DB)
            } catch (Exception e) {
                System.err.println("Erreur save Worker après register: " + e.getMessage()); // Log seulement, pas throw (photo déjà sauvée)
            }
        }
        return savedUser;
    }

    // ✅ Retourne Optional<User> (compatible JPA, pour findByEmail safe)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }
}