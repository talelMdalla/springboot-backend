package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.Application;
import com.example.khedmabackend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> postuler(@RequestBody Map<String, Long> request) {
        try {
            Long workerId = request.get("workerId");
            Long publicationId = request.get("publicationId");
            Application application = applicationService.postuler(null, null); // From DB
            Map<String, Object> response = Map.of("success", true, "application", application);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/check/{workerId}/{publicationId}")
    public ResponseEntity<Map<String, Boolean>> hasApplied(@PathVariable Long workerId, @PathVariable Long publicationId) {
        boolean applied = applicationService.hasApplied(null, null); // From DB
        return ResponseEntity.ok(Map.of("applied", applied));
    }
}