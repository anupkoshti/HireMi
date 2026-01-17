package com.example.HireMi.controller;

import com.example.HireMi.dto.*;
import com.example.HireMi.models.Interview;
import com.example.HireMi.models.Message;
import com.example.HireMi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/recruiter")
@CrossOrigin(origins = "*")
public class RecruiterController {

    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private InterviewService interviewService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private HiringAnalyticsService analyticsService;

    // ==================== CANDIDATE DISCOVERY ====================
    
    @PostMapping("/candidates/search")
    public ResponseEntity<Page<CandidateProfileDto>> searchCandidates(@RequestBody CandidateSearchDto searchDto) {
        try {
            Page<CandidateProfileDto> candidates = candidateService.searchCandidates(searchDto);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<CandidateProfileDto> getCandidateProfile(@PathVariable String candidateId) {
        Optional<CandidateProfileDto> candidate = candidateService.getCandidateProfile(candidateId);
        return candidate.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/candidates/recent")
    public ResponseEntity<List<CandidateProfileDto>> getRecentCandidates(@RequestParam(defaultValue = "10") int limit) {
        List<CandidateProfileDto> candidates = candidateService.getRecentCandidates(limit);
        return ResponseEntity.ok(candidates);
    }
    
    @GetMapping("/candidates/active")
    public ResponseEntity<List<CandidateProfileDto>> getActiveCandidates(@RequestParam(defaultValue = "10") int limit) {
        List<CandidateProfileDto> candidates = candidateService.getActiveCandidates(limit);
        return ResponseEntity.ok(candidates);
    }

    // ==================== INTERVIEW MANAGEMENT ====================
    
    @PostMapping("/interviews/schedule")
    public ResponseEntity<Interview> scheduleInterview(@RequestBody InterviewDto interviewDto) {
        try {
            Interview interview = interviewService.scheduleInterview(interviewDto);
            
            // Send interview invitation message
            messageService.sendInterviewInvite(
                interviewDto.getRecruiterId(),
                interviewDto.getRecruiterName(),
                interviewDto.getRecruiterEmail(),
                interviewDto.getCandidateId(),
                interviewDto.getCandidateName(),
                interviewDto.getCandidateEmail(),
                interviewDto.getJobRole(),
                interviewDto.getScheduledDateTime(),
                interviewDto.getMeetingLink()
            );
            
            return ResponseEntity.ok(interview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/interviews/{interviewId}/status")
    public ResponseEntity<Interview> updateInterviewStatus(
            @PathVariable String interviewId,
            @RequestParam String status,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String decision) {
        
        Optional<Interview> interview = interviewService.updateInterviewStatus(interviewId, status, feedback, rating, decision);
        return interview.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/interviews/candidate/{candidateId}")
    public ResponseEntity<Page<Interview>> getCandidateInterviews(
            @PathVariable String candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Interview> interviews = interviewService.getInterviewsByCandidate(candidateId, page, size);
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/interviews/recruiter/{recruiterId}")
    public ResponseEntity<Page<Interview>> getRecruiterInterviews(
            @PathVariable String recruiterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Interview> interviews = interviewService.getInterviewsByRecruiter(recruiterId, page, size);
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/interviews/upcoming/{recruiterId}")
    public ResponseEntity<List<Interview>> getUpcomingInterviews(
            @PathVariable String recruiterId,
            @RequestParam(defaultValue = "7") int days) {
        
        List<Interview> interviews = interviewService.getUpcomingInterviews(recruiterId, days);
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/interviews/today/{recruiterId}")
    public ResponseEntity<List<Interview>> getTodaysInterviews(@PathVariable String recruiterId) {
        List<Interview> interviews = interviewService.getTodaysInterviews(recruiterId);
        return ResponseEntity.ok(interviews);
    }
    
    @PutMapping("/interviews/{interviewId}/reschedule")
    public ResponseEntity<String> rescheduleInterview(
            @PathVariable String interviewId,
            @RequestParam String newDateTime) {
        
        try {
            LocalDateTime dateTime = LocalDateTime.parse(newDateTime);
            boolean success = interviewService.rescheduleInterview(interviewId, dateTime);
            
            if (success) {
                return ResponseEntity.ok("Interview rescheduled successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }
    
    @PutMapping("/interviews/{interviewId}/cancel")
    public ResponseEntity<String> cancelInterview(
            @PathVariable String interviewId,
            @RequestParam String reason) {
        
        boolean success = interviewService.cancelInterview(interviewId, reason);
        
        if (success) {
            return ResponseEntity.ok("Interview cancelled successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== MESSAGING ====================
    
    @PostMapping("/messages/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageDto messageDto) {
        try {
            Message message = messageService.sendMessage(messageDto);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/messages/follow-up")
    public ResponseEntity<Message> sendFollowUp(
            @RequestParam String recruiterId,
            @RequestParam String recruiterName,
            @RequestParam String recruiterEmail,
            @RequestParam String candidateId,
            @RequestParam String candidateName,
            @RequestParam String candidateEmail,
            @RequestParam String content,
            @RequestParam(required = false) String threadId) {
        
        try {
            Message message = messageService.sendFollowUpMessage(
                recruiterId, recruiterName, recruiterEmail,
                candidateId, candidateName, candidateEmail,
                content, threadId
            );
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/messages/received/{recipientId}")
    public ResponseEntity<Page<Message>> getReceivedMessages(
            @PathVariable String recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Message> messages = messageService.getMessagesByRecipient(recipientId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/messages/sent/{senderId}")
    public ResponseEntity<Page<Message>> getSentMessages(
            @PathVariable String senderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Message> messages = messageService.getMessagesBySender(senderId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/messages/thread/{threadId}")
    public ResponseEntity<List<Message>> getMessageThread(@PathVariable String threadId) {
        List<Message> messages = messageService.getMessageThread(threadId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/messages/unread/{recipientId}")
    public ResponseEntity<List<Message>> getUnreadMessages(@PathVariable String recipientId) {
        List<Message> messages = messageService.getUnreadMessages(recipientId);
        return ResponseEntity.ok(messages);
    }
    
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable String messageId) {
        boolean success = messageService.markAsRead(messageId);
        
        if (success) {
            return ResponseEntity.ok("Message marked as read");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/messages/thread/{threadId}/read")
    public ResponseEntity<String> markThreadAsRead(
            @PathVariable String threadId,
            @RequestParam String recipientId) {
        
        boolean success = messageService.markThreadAsRead(threadId, recipientId);
        return ResponseEntity.ok("Thread marked as read");
    }
    
    @GetMapping("/messages/unread-count/{recipientId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String recipientId) {
        long count = messageService.getUnreadCount(recipientId);
        return ResponseEntity.ok(count);
    }

    // ==================== ANALYTICS & STATISTICS ====================
    
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/analytics/overall")
    public ResponseEntity<HiringStatsDto> getOverallStats() {
        HiringStatsDto stats = analyticsService.getOverallStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/analytics/recruiter/{recruiterId}")
    public ResponseEntity<HiringStatsDto> getRecruiterStats(@PathVariable String recruiterId) {
        HiringStatsDto stats = analyticsService.getRecruiterStats(recruiterId);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/analytics/record-metrics/{recruiterId}")
    public ResponseEntity<String> recordDailyMetrics(@PathVariable String recruiterId) {
        analyticsService.recordDailyMetrics(recruiterId);
        return ResponseEntity.ok("Daily metrics recorded successfully");
    }

    // ==================== QUICK ACTIONS ====================
    
    @PostMapping("/quick-actions/bulk-message")
    public ResponseEntity<String> sendBulkMessage(
            @RequestParam String recruiterId,
            @RequestParam String recruiterName,
            @RequestParam String recruiterEmail,
            @RequestParam List<String> candidateIds,
            @RequestParam String subject,
            @RequestParam String content) {
        
        try {
            // This would need candidate details - simplified for now
            return ResponseEntity.ok("Bulk messages sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to send bulk messages");
        }
    }
    
    @GetMapping("/download/resume/{candidateId}")
    public ResponseEntity<String> downloadResume(@PathVariable String candidateId) {
        // This would return the actual file download
        // For now, returning a placeholder response
        return ResponseEntity.ok("Resume download link for candidate: " + candidateId);
    }
}