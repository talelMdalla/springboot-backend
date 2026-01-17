package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Review;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.repository.ReviewRepository;
import com.example.khedmabackend.repository.UserRepository; // ✅ Nouveau : Inject pour fetch client
import com.example.khedmabackend.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {
    private static final Logger logger = LoggerFactory.getLogger(WorkerService.class);

    private final WorkerRepository workerRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository; // ✅ Nouveau : Pour fetch client par ID

    public WorkerService(WorkerRepository workerRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.workerRepository = workerRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    // ✅ Fix : Workers par catégorie avec JOIN (charge profileImage)
    public List<Worker> findAvailableByCategory(String category) {
        logger.info("Query workers category: {}", category);
        List<Worker> workers = workerRepository.findAllByCategoryWithUser(category); // ✅ Utilise query JOIN pour inclure profileImage
        logger.info("Retourné {} workers pour category {}", workers.size(), category);
        return workers;
    }

    public List<Worker> findByCategoryAndGovernorate(String category, String governorate) {
        logger.info("Query workers category {} governorate {}", category, governorate);
        List<Worker> workers = workerRepository.findByCategoryAndGovernorateWithUser(category, governorate); // ✅ Utilise query JOIN pour inclure profileImage
        logger.info("Retourné {} workers pour {} - {}", workers.size(), category, governorate);
        return workers;
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public Worker findById(Long id) {
        return workerRepository.findById(id).orElse(null);
    }

    // ✅ Nouveau : Ajouter avis (étoiles + commentaire) pour worker
    public Review addReview(Long workerId, Long clientId, int stars, String comment) {
        logger.info("Ajout avis pour workerId {}, clientId {}, stars {}, comment '{}'", workerId, clientId, stars, comment);
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker non trouvé: " + workerId));
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + clientId)); // ✅ Fix : Fetch réel via UserRepository

        Review review = new Review(worker, client, stars, comment);
        Review savedReview = reviewRepository.save(review);

        // ✅ Calculer nouvelle moyenne et count
        Double newAverage = reviewRepository.averageStarsByWorkerId(workerId);
        long newCount = reviewRepository.countByWorkerId(workerId);

        worker.setAverageRating(newAverage != null ? newAverage : 0.0);
        worker.setReviewCount((int) newCount);
        workerRepository.save(worker); // Update worker avec nouvelle moyenne/count

        logger.info("Avis ajouté ID {}, nouvelle moyenne {} ({})", savedReview.getId(), worker.getAverageRating(), worker.getReviewCount());
        return savedReview;
    }

    // ✅ Nouveau : Récupérer avis d'un worker (liste avec client info)
    public List<Review> getReviewsByWorkerId(Long workerId) {
        logger.info("Récup avis pour workerId {}", workerId);
        List<Review> reviews = reviewRepository.findByWorkerIdWithClient(workerId);
        logger.info("Retourné {} avis pour worker {}", reviews.size(), workerId);
        return reviews;
    }
}