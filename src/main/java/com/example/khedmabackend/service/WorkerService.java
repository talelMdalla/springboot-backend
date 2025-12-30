package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Worker;
import com.example.khedmabackend.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {
    private static final Logger logger = LoggerFactory.getLogger(WorkerService.class);
    private final WorkerRepository workerRepository;

    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    // ✅ Fix : Workers par catégorie avec JOIN (charge profileImage)
    public List<Worker> findAvailableByCategory(String category) {
        logger.info("Query workers category: {}", category);
        List<Worker> workers = workerRepository.findAllByCategoryWithUser(category); // ✅ Fix : Utilise query JOIN pour inclure profileImage
        logger.info("Retourné {} workers pour category {}", workers.size(), category);
        return workers;
    }

    public List<Worker> findByCategoryAndGovernorate(String category, String governorate) {
        logger.info("Query workers category {} governorate {}", category, governorate);
        List<Worker> workers = workerRepository.findByCategoryAndGovernorateWithUser(category, governorate); // ✅ Fix : Utilise query JOIN pour inclure profileImage
        logger.info("Retourné {} workers pour {} - {}", workers.size(), category, governorate);
        return workers;
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public Worker findById(Long id) {
        return workerRepository.findById(id).orElse(null);
    }
}