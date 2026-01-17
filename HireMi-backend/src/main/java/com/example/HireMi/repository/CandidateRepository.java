package com.example.HireMi.repository;

import com.example.HireMi.models.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    
    // Basic search
    Optional<Candidate> findByEmail(String email);
    List<Candidate> findByStatus(String status);
    
    // Advanced filtering
    Page<Candidate> findByDesiredRoleContainingIgnoreCase(String role, Pageable pageable);
    Page<Candidate> findBySkillsContainingIgnoreCase(String skill, Pageable pageable);
    Page<Candidate> findByExperienceLevel(String experienceLevel, Pageable pageable);
    Page<Candidate> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    // Complex queries
    @Query("{ 'skills': { $in: ?0 }, 'experienceLevel': ?1, 'status': 'ACTIVE' }")
    Page<Candidate> findBySkillsInAndExperienceLevel(List<String> skills, String experienceLevel, Pageable pageable);
    
    @Query("{ 'desiredRole': { $regex: ?0, $options: 'i' }, 'totalExperienceYears': { $gte: ?1, $lte: ?2 } }")
    Page<Candidate> findByRoleAndExperienceRange(String role, Integer minExp, Integer maxExp, Pageable pageable);
    
    @Query("{ 'skills': { $in: ?0 }, 'availabilityStatus': 'AVAILABLE', 'status': 'ACTIVE' }")
    List<Candidate> findAvailableCandidatesBySkills(List<String> skills);
    
    @Query("{ 'primarySkills': { $in: ?0 }, 'location': { $regex: ?1, $options: 'i' }, 'openToRemote': ?2 }")
    Page<Candidate> findByPrimarySkillsAndLocationAndRemote(List<String> skills, String location, Boolean remote, Pageable pageable);
    
    // Statistics queries
    @Query(value = "{ 'status': 'ACTIVE' }", count = true)
    Long countActiveCandidates();
    
    @Query(value = "{ 'availabilityStatus': 'AVAILABLE' }", count = true)
    Long countAvailableCandidates();
    
    @Query(value = "{ 'profileCreated': { $gte: ?0 } }", count = true)
    Long countCandidatesRegisteredAfter(LocalDateTime date);
    
    // Recent activity
    List<Candidate> findTop10ByOrderByLastActiveDesc();
    List<Candidate> findTop10ByOrderByProfileCreatedDesc();
}