package com.example.HireMi.service;

import com.example.HireMi.dto.HiringStatsDto;
import com.example.HireMi.models.HiringMetrics;
import com.example.HireMi.repository.CandidateRepository;
import com.example.HireMi.repository.HiringMetricsRepository;
import com.example.HireMi.repository.InterviewRepository;
import com.example.HireMi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HiringAnalyticsService {

    @Autowired
    private HiringMetricsRepository hiringMetricsRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private MessageRepository messageRepository;

    public HiringStatsDto getOverallStats() {
        HiringStatsDto stats = new HiringStatsDto();
        
        // Basic counts
        stats.setTotalCandidates(candidateRepository.count());
        stats.setActiveCandidates(candidateRepository.countActiveCandidates());
        stats.setAvailableCandidates(candidateRepository.countAvailableCandidates());
        
        // Recent activity (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        stats.setNewCandidatesThisMonth(candidateRepository.countCandidatesRegisteredAfter(thirtyDaysAgo));
        
        // Interview stats
        stats.setTotalInterviews(interviewRepository.count());
        stats.setCompletedInterviews(interviewRepository.countByStatus("COMPLETED"));
        stats.setScheduledInterviews(interviewRepository.countByStatus("SCHEDULED"));
        
        // Success rates
        long totalMessages = messageRepository.count();
        long interviewInvites = messageRepository.countByMessageType("INTERVIEW_INVITE");
        long completedInterviews = interviewRepository.countByStatus("COMPLETED");
        
        if (interviewInvites > 0) {
            stats.setContactToInterviewRate((double) completedInterviews / interviewInvites * 100);
        }
        
        // Top skills in demand
        stats.setTopSkills(getTopSkillsFromCandidates());
        
        // Recent hiring trends
        stats.setHiringTrends(getRecentHiringTrends());
        
        return stats;
    }

    public HiringStatsDto getRecruiterStats(String recruiterId) {
        HiringStatsDto stats = new HiringStatsDto();
        
        // Recruiter-specific metrics
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        stats.setTotalInterviews(interviewRepository.countByRecruiterId(recruiterId));
        stats.setCompletedInterviews(interviewRepository.countByRecruiterIdAndStatus(recruiterId, "COMPLETED"));
        stats.setScheduledInterviews(interviewRepository.countByRecruiterIdAndStatus(recruiterId, "SCHEDULED"));
        
        // Messages sent
        long messagesSent = messageRepository.countBySenderIdAndSentAtBetween(recruiterId, thirtyDaysAgo, LocalDateTime.now());
        stats.setMessagesSent(messagesSent);
        
        // Success rates for this recruiter
        long recruiterInterviewInvites = messageRepository.countBySenderIdAndMessageType(recruiterId, "INTERVIEW_INVITE");
        long recruiterCompletedInterviews = interviewRepository.countByRecruiterIdAndStatus(recruiterId, "COMPLETED");
        
        if (recruiterInterviewInvites > 0) {
            stats.setContactToInterviewRate((double) recruiterCompletedInterviews / recruiterInterviewInvites * 100);
        }
        
        // Recent activity
        stats.setRecentInterviews(interviewRepository.countByRecruiterIdAndCreatedAtAfter(recruiterId, thirtyDaysAgo));
        
        return stats;
    }

    public void recordDailyMetrics(String recruiterId) {
        LocalDate today = LocalDate.now();
        Optional<HiringMetrics> existingMetrics = hiringMetricsRepository.findByRecruiterIdAndDate(recruiterId, today);
        
        HiringMetrics metrics;
        if (existingMetrics.isPresent()) {
            metrics = existingMetrics.get();
        } else {
            metrics = new HiringMetrics();
            metrics.setRecruiterId(recruiterId);
            metrics.setDate(today);
        }
        
        // Calculate daily metrics
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        // Count activities for today
        long interviewsToday = interviewRepository.countByRecruiterIdAndCreatedAtBetween(recruiterId, startOfDay, endOfDay);
        long messagestoday = messageRepository.countBySenderIdAndSentAtBetween(recruiterId, startOfDay, endOfDay);
        
        metrics.setInterviewsScheduled((int) interviewsToday);
        metrics.setCandidatesContacted((int) messagestoday);
        
        // Calculate success rates
        updateSuccessRates(metrics, recruiterId);
        
        hiringMetricsRepository.save(metrics);
    }

    public List<HiringMetrics> getMetricsHistory(String recruiterId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return hiringMetricsRepository.findByRecruiterIdAndDateBetweenOrderByDateDesc(recruiterId, startDate, endDate);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Quick stats
        dashboard.put("totalCandidates", candidateRepository.count());
        dashboard.put("activeCandidates", candidateRepository.countActiveCandidates());
        dashboard.put("availableCandidates", candidateRepository.countAvailableCandidates());
        dashboard.put("totalInterviews", interviewRepository.count());
        
        // Recent activity
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        dashboard.put("newCandidatesThisWeek", candidateRepository.countCandidatesRegisteredAfter(weekAgo));
        dashboard.put("interviewsThisWeek", interviewRepository.countByCreatedAtAfter(weekAgo));
        
        // Top performing metrics
        dashboard.put("topSkills", getTopSkillsFromCandidates());
        dashboard.put("recentCandidates", candidateRepository.findTop10ByOrderByProfileCreatedDesc());
        
        return dashboard;
    }

    private void updateSuccessRates(HiringMetrics metrics, String recruiterId) {
        // Calculate overall success rates for this recruiter
        long totalInvites = messageRepository.countBySenderIdAndMessageType(recruiterId, "INTERVIEW_INVITE");
        long totalInterviews = interviewRepository.countByRecruiterId(recruiterId);
        long completedInterviews = interviewRepository.countByRecruiterIdAndStatus(recruiterId, "COMPLETED");
        
        if (totalInvites > 0) {
            metrics.setContactToInterviewRate((double) totalInterviews / totalInvites);
        }
        
        if (totalInterviews > 0) {
            metrics.setInterviewToOfferRate((double) completedInterviews / totalInterviews);
        }
    }

    private Map<String, Integer> getTopSkillsFromCandidates() {
        // This would ideally be done with MongoDB aggregation
        // For now, returning a simple map
        Map<String, Integer> topSkills = new HashMap<>();
        topSkills.put("Java", 150);
        topSkills.put("JavaScript", 120);
        topSkills.put("Python", 100);
        topSkills.put("React", 90);
        topSkills.put("Spring Boot", 80);
        return topSkills;
    }

    private Map<String, Integer> getRecentHiringTrends() {
        Map<String, Integer> trends = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            
            long hiresCount = interviewRepository.countByStatusAndUpdatedAtBetween("COMPLETED", startOfDay, endOfDay);
            trends.put(date.toString(), (int) hiresCount);
        }
        
        return trends;
    }
}