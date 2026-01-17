package com.example.HireMi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hiring_metrics")
public class HiringMetrics {
    @Id
    private String id;
    
    private String recruiterId;
    private LocalDate date;
    
    // Daily metrics
    private Integer profilesViewed;
    private Integer candidatesContacted;
    private Integer interviewsScheduled;
    private Integer interviewsCompleted;
    private Integer offersExtended;
    private Integer hiresCompleted;
    
    // Success rates
    private Double contactToInterviewRate;
    private Double interviewToOfferRate;
    private Double offerToHireRate;
    private Double overallSuccessRate;
    
    // Time metrics (in days)
    private Double averageTimeToInterview;
    private Double averageTimeToHire;
    
    // Role-wise breakdown
    private Map<String, Integer> hiresByRole;
    private Map<String, Integer> interviewsByRole;
    
    // Skill-wise breakdown
    private Map<String, Integer> hiresBySkill;
}