package com.example.HireMi.dto;

import com.example.HireMi.models.Candidate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileDto {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String currentRole;
    private String desiredRole;
    private Integer totalExperienceYears;
    private String experienceLevel;
    
    private List<String> primarySkills;
    private Map<String, Integer> skillProficiency;
    private List<Candidate.Education> education;
    private List<Candidate.WorkExperience> workExperience;
    
    private String profileSummary;
    private String availabilityStatus;
    private Double expectedSalary;
    private String preferredLocation;
    private Boolean openToRemote;
    private String noticePeriod;
    
    private Integer profileViews;
    private LocalDateTime lastActive;
    private LocalDateTime profileCreated;
    
    private String status;
    private List<String> tags;
    
    // Additional fields for recruiter view
    private Boolean hasResume;
    private Integer interviewCount;
    private String lastInteractionDate;
    private Double matchScore; // Calculated based on search criteria
}