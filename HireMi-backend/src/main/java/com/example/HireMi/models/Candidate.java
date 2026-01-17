package com.example.HireMi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "candidates")
public class Candidate {
    @Id
    private String id;
    
    @Indexed
    private String email;
    
    private String name;
    private String phone;
    private String location;
    private String currentRole;
    private String desiredRole;
    
    // Experience details
    private Integer totalExperienceYears;
    private String experienceLevel; // FRESHER, JUNIOR, MID, SENIOR, LEAD
    
    // Skills and competencies
    private List<String> skills;
    private List<String> primarySkills; // Top 5 most relevant skills
    private Map<String, Integer> skillProficiency; // skill -> years of experience
    
    // Education
    private List<Education> education;
    
    // Work Experience
    private List<WorkExperience> workExperience;
    
    // Resume and profile data
    private String resumeFileId; // GridFS file ID for original resume
    private String profileSummary;
    private Map<String, Object> parsedResumeData; // AI-parsed data
    
    // Availability and preferences
    private String availabilityStatus; // AVAILABLE, INTERVIEWING, NOT_LOOKING
    private Double expectedSalary;
    private String preferredLocation;
    private Boolean openToRemote;
    private String noticePeriod;
    
    // Recruiter interaction data
    private Integer profileViews;
    private LocalDateTime lastActive;
    private LocalDateTime profileCreated;
    private LocalDateTime profileUpdated;
    
    // Status tracking
    private String status; // ACTIVE, INACTIVE, HIRED, BLACKLISTED
    private List<String> tags; // Custom tags for categorization
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
        private String degree;
        private String institution;
        private String fieldOfStudy;
        private String graduationYear;
        private String grade;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkExperience {
        private String title;
        private String company;
        private String duration;
        private String startDate;
        private String endDate;
        private String description;
        private List<String> technologies;
        private Boolean currentJob;
    }
}