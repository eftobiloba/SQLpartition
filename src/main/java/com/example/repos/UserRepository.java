package com.example.repos;

import com.example.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findTopByOrderByUserIdDesc();
}

