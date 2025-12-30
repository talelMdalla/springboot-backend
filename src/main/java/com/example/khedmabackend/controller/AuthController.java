package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.User;
import com.example.khedmabackend.service.UserService;
import com.example.khedmabackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            System.out.println("Debug register: Email = " + user.getEmail() + ", Password raw = " + user.getPassword().substring(0, 5) + "..."); // Log debug
            User savedUser = userService.saveUser(user); // Hash + save via service
            String token = jwtUtil.generateToken(savedUser.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", savedUser);
            System.out.println("Debug register: User saved ID = " + savedUser.getId() + ", Password hash starts with = " + savedUser.getPassword().substring(0, 10) + "..."); // Log debug
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Debug register error: " + e.getMessage()); // Log debug
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur register: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            if (email == null || password == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email/password requis.");
                return ResponseEntity.badRequest().body(error);
            }
            System.out.println("Debug login: Email = " + email + ", Password raw length = " + password.length()); // Log debug
            // Authentifie (Spring Security vérifie hash)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            Optional<User> userOptional = userService.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("Debug login: User found ID = " + user.getId() + ", Password hash starts with = " + user.getPassword().substring(0, 10) + "..."); // Log debug
                String token = jwtUtil.generateToken(authentication.getName());
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Erreur login: Les identifications sont erronées");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            System.out.println("Debug login error: " + e.getMessage()); // Log debug
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur login: Les identifications sont erronées");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ Fix : Catch Exception (capture RuntimeException Worker), log, retourne 200 si sauvé
    @PostMapping("/upload-profile-image")
    @PreAuthorize("hasRole('CLIENT') or hasRole('WORKER')")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("email") String email, @RequestParam("image") MultipartFile image) {
        try {
            // Sauve image dans /uploads (créé dossier si absent)
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = UUID.randomUUID().toString() + ".jpg"; // Nom unique
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, image.getBytes()); // Sauve fichier

            // Update user avec chemin image
            Optional<User> userOptional = userService.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("Debug upload: User found ID = " + user.getId() + ", Current profileImage = " + user.getProfileImage() + ", Password hash starts with = " + user.getPassword().substring(0, 10) + "..."); // Log debug
                user.setProfileImage("/uploads/" + fileName); // ✅ Set colonne profile_image
                user = userService.saveUser(user); // Sauvegarde en DB (peut throw pour WORKER validation)
                System.out.println("Debug upload: User saved with new profileImage = " + user.getProfileImage() + ", Password hash unchanged = " + user.getPassword().substring(0, 10) + "..."); // Log debug
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("path", "/uploads/" + fileName); // Retourne path clean pour frontend
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User non trouvé pour email: " + email);
                return ResponseEntity.badRequest().body(error);
            }
        } catch (IOException e) {
            System.err.println("Erreur IOException upload: " + e.getMessage()); // ✅ Log spécifique
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur fichier: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        } catch (Exception e) {
            System.err.println("Erreur générale upload: " + e.getMessage()); // ✅ Log pour Worker RuntimeException
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur serveur (photo sauvée): " + e.getMessage());
            return ResponseEntity.internalServerError().body(response); // 500, mais message informatif
        }
    }
}