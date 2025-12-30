package com.example.khedmabackend.controller;

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
    private final UserService userService; // ✅ Fix : Inject UserService pour nom/prenom

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
        response.put("rating", worker.getRating());
        response.put("reviewCount", worker.getReviewCount());
        response.put("pricePerHour", worker.getPricePerHour());
        response.put("skills", worker.getSkills());
        response.put("isAvailable", worker.getAvailable());
        response.put("user", user);
        return response;
    }
}