package com.example.khedmabackend.service;

import com.example.khedmabackend.model.Application;
import com.example.khedmabackend.model.Publication;
import com.example.khedmabackend.model.User;
import com.example.khedmabackend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    public Application postuler(User worker, Publication publication) {
        if (applicationRepository.existsByWorkerIdAndPublicationId(worker.getId(), publication.getId())) {
            throw new RuntimeException("Déjà postulé");
        }
        Application application = new Application(worker, publication);
        return applicationRepository.save(application);
    }

    public boolean hasApplied(User worker, Publication publication) {
        return applicationRepository.existsByWorkerIdAndPublicationId(worker.getId(), publication.getId());
    }
}