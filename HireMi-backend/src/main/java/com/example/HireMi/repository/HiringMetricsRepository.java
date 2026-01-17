package com.example.HireMi.repository;

import com.example.HireMi.models.HiringMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HiringMetricsRepository extends MongoRepository<HiringMetrics, String> {
    
    Optional<HiringMetrics> findByRecruiterIdAndDate(String recruiterId, LocalDate date);
    List<HiringMetrics> findByRecruiterId(String recruiterId);
    
    @Query("{ 'recruiterId': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    List<HiringMetrics> findByRecruiterAndDateRange(String recruiterId, LocalDate startDate, LocalDate endDate);
    
    List<HiringMetrics> findByRecruiterIdAndDateBetweenOrderByDateDesc(String recruiterId, LocalDate startDate, LocalDate endDate);
    
    @Query("{ 'date': { $gte: ?0, $lte: ?1 } }")
    List<HiringMetrics> findByDateRange(LocalDate startDate, LocalDate endDate);
}