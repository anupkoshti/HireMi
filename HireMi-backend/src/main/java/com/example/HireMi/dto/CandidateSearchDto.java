package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSearchDto {
    private String keyword;
    private String role;
    private List<String> skills;
    private String experienceLevel;
    private Integer minExperience;
    private Integer maxExperience;
    private String location;
    private Boolean openToRemote;
    private String availabilityStatus;
    private Double minSalary;
    private Double maxSalary;
    private List<String> tags;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "lastActive";
    private String sortDirection = "desc";
}