package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.Review; // ✅ Nouveau import pour Review
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.service.UserService;
import com.example.khedmabackend.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "*")
public class WorkerController {
    private final WorkerService workerService;
    private final UserService userService;

    // ✅ Fix : Inject UserService pour nom/prenom
    public WorkerController(WorkerService workerService, UserService userService) {
        this.workerService = workerService;
        this.userService = userService;
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Map<String, Object>>> getWorkersByCategory(@PathVariable String category) {
        List<Worker> workers = workerService.findAvailableByCategory(category);
        List<Map<String, Object>> response = workers.stream().map(this::mapWorkerToResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchWorkers(@RequestParam String category, @RequestParam String governorate) {
        List<Worker> workers = workerService.findByCategoryAndGovernorate(category, governorate);
        List<Map<String, Object>> response = workers.stream().map(this::mapWorkerToResponse).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createWorker(@RequestBody Worker worker) {
        Worker saved = workerService.saveWorker(worker);
        Map<String, Object> response = mapWorkerToResponse(saved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getWorkerById(@PathVariable Long id) {
        Worker worker = workerService.findById(id);
        if (worker != null) {
            return ResponseEntity.ok(mapWorkerToResponse(worker));
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Nouveau : Ajouter avis (étoiles + commentaire) pour worker
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> addReview(@PathVariable Long id, @RequestBody Map<String, Object> reviewData) {
        try {
            String clientIdStr = (String) reviewData.get("clientId");
            Long clientId = Long.parseLong(clientIdStr);
            int stars = ((Number) reviewData.get("stars")).intValue();
            String comment = (String) reviewData.get("comment");

            Review review = workerService.addReview(id, clientId, stars, comment);

            // ✅ Fix : Retourne Map flat (pas full Review/Worker pour éviter nesting long)
            Map<String, Object> reviewResponse = new HashMap<>();
            reviewResponse.put("id", review.getId());
            reviewResponse.put("stars", review.getStars());
            reviewResponse.put("comment", review.getComment());
            reviewResponse.put("createdAt", review.getCreatedAt().toString());
            reviewResponse.put("workerId", review.getWorker().getId()); // Seulement ID worker

            // Flat client (seulement nom/prénom/photo, pas full User)
            User client = review.getClient();
            reviewResponse.put("clientNom", client.getNom());
            reviewResponse.put("clientPrenom", client.getPrenom());
            reviewResponse.put("clientProfileImage", client.getProfileImage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("review", reviewResponse); // Léger, pas nesting
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erreur ajout avis: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ Nouveau : Récupérer liste avis d'un worker (flat Maps pour éviter nesting)
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Map<String, Object>>> getReviewsByWorker(@PathVariable Long id) {
        List<Review> reviews = workerService.getReviewsByWorkerId(id);
        List<Map<String, Object>> reviewList = reviews.stream().map(this::mapReviewToResponse).toList();
        return ResponseEntity.ok(reviewList);
    }

    // ✅ Nouveau : Map flat pour Review (pas full worker/user pour JSON léger)
    private Map<String, Object> mapReviewToResponse(Review review) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", review.getId());
        response.put("stars", review.getStars());
        response.put("comment", review.getComment());
        response.put("createdAt", review.getCreatedAt().toString());
        response.put("workerId", review.getWorker().getId()); // Seulement ID worker

        // Flat client (seulement nom/prénom/photo, pas full User)
        User client = review.getClient();
        response.put("clientNom", client.getNom());
        response.put("clientPrenom", client.getPrenom());
        response.put("clientProfileImage", client.getProfileImage());

        return response;
    }

    // ✅ Fix : Map avec nom/prenom from User (query si null, fallback DB avec user_id correct)
    private Map<String, Object> mapWorkerToResponse(Worker worker) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", worker.getId());
        // ✅ Fix : Nom/prenom from User (query si null)
        User user = worker.getUser();
        if (user == null) {
            // ✅ Fix : Utilise user_id de Worker si disponible
            Long userId = worker.getUserId(); // Assume Worker a getUserId()
            if (userId != null) {
                User userFromService = userService.findById(userId); // Retourne User direct (pas Optional)
                user = userFromService; // ✅ Fix : Assign direct User (pas Optional.orElse)
            }
        }
        response.put("nom", user != null ? user.getNom() : "N/A"); // Safe
        response.put("prenom", user != null ? user.getPrenom() : "N/A"); // Safe
        response.put("category", worker.getCategory());
        response.put("description", worker.getDescription());
        response.put("governorate", worker.getGovernorate());
        response.put("phone", user != null ? user.getPhone() : "");
        response.put("email", user != null ? user.getEmail() : "");
        response.put("location", user != null ? user.getAdresse() : "");
        response.put("dateNaissance", worker.getDateNaissance());
        response.put("averageRating", worker.getAverageRating()); // ✅ Utilise averageRating (moyen)
        response.put("reviewCount", worker.getReviewCount()); // ✅ Nombre avis
        response.put("pricePerHour", worker.getPricePerHour());
        response.put("skills", worker.getSkills());
        response.put("isAvailable", worker.getAvailable());
        response.put("user", user);
        return response;
    }
}