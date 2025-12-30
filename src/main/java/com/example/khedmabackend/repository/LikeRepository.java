package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.Like;
import com.example.khedmabackend.model.Publication;
import com.example.khedmabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPublicationAndUser(Publication publication, User user);
}