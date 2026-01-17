package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiringStatsDto {
    // Basic counts
    private Long totalCandidates;
    private Long activeCandidates;
    private Long availableCandidates;
    private Long newCandidatesThisMonth;
    
    // Interview statistics
    private Long totalInterviews;
    private Long completedInterviews;
    private Long scheduledInterviews;
    private Long recentInterviews;
    
    // Message statistics
    private Long messagesSent;
    private Long unreadMessages;
    
    // Success rates (percentages)
    private Double contactToInterviewRate;
    private Double interviewToOfferRate;
    private Double offerToHireRate;
    private Double overallSuccessRate;
    
    // Time metrics (in days)
    private Double averageTimeToInterview;
    private Double averageTimeToHire;
    
    // Breakdown data
    private Map<String, Integer> topSkills;
    private Map<String, Integer> hiresByRole;
    private Map<String, Integer> hiringTrends;
    
    // Performance metrics
    private Integer profilesViewedToday;
    private Integer candidatesContactedToday;
    private Integer interviewsScheduledToday;
}