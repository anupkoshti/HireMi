package com.example.HireMi.repository;

import com.example.HireMi.models.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    // Additional custom queries can be added here
}
