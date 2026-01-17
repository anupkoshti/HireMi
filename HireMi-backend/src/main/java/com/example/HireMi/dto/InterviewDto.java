package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDto {
    private String id;
    
    private String candidateId;
    private String candidateName;
    private String candidateEmail;
    
    private String recruiterId;
    private String recruiterName;
    private String recruiterEmail;
    
    private String jobRole;
    private String interviewType; // PHONE, VIDEO, IN_PERSON, TECHNICAL, HR
    private String status; // SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED, NO_SHOW
    
    private LocalDateTime scheduledDateTime;
    private Integer durationMinutes;
    private String meetingLink;
    private String location;
    
    private String feedback;
    private Integer rating; // 1-5 scale
    private String decision; // PROCEED, REJECT, ON_HOLD
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String notes;
}