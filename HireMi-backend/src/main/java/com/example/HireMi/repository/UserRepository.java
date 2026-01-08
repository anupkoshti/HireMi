package com.example.HireMi.repository;

import com.example.HireMi.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Find user by email
    Optional<User> findByEmail(String email);
}
