package com.example.khedmabackend.repository;

import com.example.khedmabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ✅ Query pour findByEmail (alias u, @Param – safe Optional)
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    // Query pour findAllByType (déjà là, pour lister clients/workers)
    @Query("SELECT u FROM User u WHERE u.type = :type")
    List<User> findAllByType(@Param("type") String type);
}