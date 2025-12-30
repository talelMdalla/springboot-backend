package com.example.khedmabackend.controller;

import com.example.khedmabackend.model.Publication;
import com.example.khedmabackend.model.Comment;
import com.example.khedmabackend.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publications")
@CrossOrigin(origins = "*")
public class PublicationController {
    @Autowired
    private PublicationService publicationService;

    // Créer publication (texte seul, JSON)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createPublication(@RequestBody Map<String, Object> request) {
        Long authorId = Long.parseLong(request.get("authorId").toString());
        String content = (String) request.get("content");
        Publication publication = publicationService.createPublication(authorId, content, null); // Pas image
        Map<String, Object> response = Map.of("success", true, "publication", publication);
        return ResponseEntity.ok(response);
    }

    // Créer publication avec image (multipart)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> createPublicationWithImage(
            @RequestParam("authorId") Long authorId,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Publication publication = publicationService.createPublication(authorId, content, image);
        Map<String, Object> response = Map.of("success", true, "publication", publication);
        return ResponseEntity.ok(response);
    }

    // Like publication (toggle, JSON)
    @PutMapping(path = "/{id}/like", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> likePublication(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        Publication updated = publicationService.likePublication(id, userId);
        Map<String, Object> response = Map.of("success", true, "publication", updated);
        return ResponseEntity.ok(response);
    }

    // Ajouter commentaire (JSON)
    @PostMapping(path = "/{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(request.get("userId").toString());
        String comment = (String) request.get("comment");
        Publication updated = publicationService.addComment(id, userId, comment);
        Map<String, Object> response = Map.of("success", true, "publication", updated);
        return ResponseEntity.ok(response);
    }

    // ✅ Nouveau : Lister commentaires pour publication (avec user info)
    @GetMapping(path = "/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        List<Comment> comments = publicationService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    @GetMapping
    public ResponseEntity<List<Publication>> getAllPublications() {
        List<Publication> publications = publicationService.getAllPublications();
        return ResponseEntity.ok(publications);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Publication>> getByType(@PathVariable String type) {
        List<Publication> publications = publicationService.getPublicationsByType(type);
        return ResponseEntity.ok(publications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getById(@PathVariable Long id) {
        Publication publication = publicationService.findById(id);
        if (publication != null) {
            return ResponseEntity.ok(publication);
        }
        return ResponseEntity.notFound().build();
    }
}