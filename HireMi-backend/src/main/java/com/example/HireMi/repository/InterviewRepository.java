package com.example.HireMi.repository;

import com.example.HireMi.models.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends MongoRepository<Interview, String> {
    
    List<Interview> findByCandidateId(String candidateId);
    Page<Interview> findByCandidateId(String candidateId, Pageable pageable);
    List<Interview> findByRecruiterId(String recruiterId);
    Page<Interview> findByRecruiterId(String recruiterId, Pageable pageable);
    List<Interview> findByStatus(String status);
    
    Page<Interview> findByRecruiterIdAndStatus(String recruiterId, String status, Pageable pageable);
    
    @Query("{ 'scheduledDateTime': { $gte: ?0, $lte: ?1 } }")
    List<Interview> findInterviewsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{ 'recruiterId': ?0, 'scheduledDateTime': { $gte: ?1, $lte: ?2 } }")
    List<Interview> findByRecruiterAndDateRange(String recruiterId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Interview> findByRecruiterIdAndScheduledDateTimeBetween(String recruiterId, LocalDateTime startDate, LocalDateTime endDate);
    List<Interview> findByRecruiterIdAndScheduledDateTimeBetweenAndStatus(String recruiterId, LocalDateTime startDate, LocalDateTime endDate, String status);
    
    @Query("{ 'status': 'SCHEDULED', 'scheduledDateTime': { $gte: ?0, $lte: ?1 } }")
    List<Interview> findUpcomingInterviews(LocalDateTime startDate, LocalDateTime endDate);
    
    // Statistics
    @Query(value = "{ 'recruiterId': ?0, 'status': 'COMPLETED' }", count = true)
    Long countCompletedInterviewsByRecruiter(String recruiterId);
    
    @Query(value = "{ 'status': 'COMPLETED', 'decision': 'PROCEED' }", count = true)
    Long countSuccessfulInterviews();
    
    @Query(value = "{ 'status': ?0 }", count = true)
    Long countByStatus(String status);
    
    @Query(value = "{ 'recruiterId': ?0 }", count = true)
    Long countByRecruiterId(String recruiterId);
    
    @Query(value = "{ 'recruiterId': ?0, 'status': ?1 }", count = true)
    Long countByRecruiterIdAndStatus(String recruiterId, String status);
    
    @Query(value = "{ 'recruiterId': ?0, 'createdAt': { $gte: ?1 } }", count = true)
    Long countByRecruiterIdAndCreatedAtAfter(String recruiterId, LocalDateTime date);
    
    @Query(value = "{ 'recruiterId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }", count = true)
    Long countByRecruiterIdAndCreatedAtBetween(String recruiterId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query(value = "{ 'createdAt': { $gte: ?0 } }", count = true)
    Long countByCreatedAtAfter(LocalDateTime date);
    
    @Query(value = "{ 'status': ?0, 'updatedAt': { $gte: ?1, $lte: ?2 } }", count = true)
    Long countByStatusAndUpdatedAtBetween(String status, LocalDateTime startDate, LocalDateTime endDate);
}