package com.example.HireMi.service;

import com.example.HireMi.dto.CandidateProfileDto;
import com.example.HireMi.dto.CandidateSearchDto;
import com.example.HireMi.models.Candidate;
import com.example.HireMi.repository.CandidateRepository;
import com.example.HireMi.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<CandidateProfileDto> searchCandidates(CandidateSearchDto searchDto) {
        Pageable pageable = createPageable(searchDto);
        
        // Build dynamic query based on search criteria
        Query query = new Query();
        Criteria criteria = new Criteria();
        
        // Add search criteria
        if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
            criteria.orOperator(
                Criteria.where("name").regex(searchDto.getKeyword(), "i"),
                Criteria.where("skills").regex(searchDto.getKeyword(), "i"),
                Criteria.where("currentRole").regex(searchDto.getKeyword(), "i"),
                Criteria.where("desiredRole").regex(searchDto.getKeyword(), "i")
            );
        }
        
        if (searchDto.getRole() != null && !searchDto.getRole().isEmpty()) {
            criteria.and("desiredRole").regex(searchDto.getRole(), "i");
        }
        
        if (searchDto.getSkills() != null && !searchDto.getSkills().isEmpty()) {
            criteria.and("skills").in(searchDto.getSkills());
        }
        
        if (searchDto.getExperienceLevel() != null) {
            criteria.and("experienceLevel").is(searchDto.getExperienceLevel());
        }
        
        if (searchDto.getMinExperience() != null || searchDto.getMaxExperience() != null) {
            Criteria expCriteria = Criteria.where("totalExperienceYears");
            if (searchDto.getMinExperience() != null) {
                expCriteria.gte(searchDto.getMinExperience());
            }
            if (searchDto.getMaxExperience() != null) {
                expCriteria.lte(searchDto.getMaxExperience());
            }
            criteria.andOperator(expCriteria);
        }
        
        if (searchDto.getLocation() != null && !searchDto.getLocation().isEmpty()) {
            criteria.and("location").regex(searchDto.getLocation(), "i");
        }
        
        if (searchDto.getOpenToRemote() != null) {
            criteria.and("openToRemote").is(searchDto.getOpenToRemote());
        }
        
        if (searchDto.getAvailabilityStatus() != null) {
            criteria.and("availabilityStatus").is(searchDto.getAvailabilityStatus());
        }
        
        // Always filter for active candidates
        criteria.and("status").is("ACTIVE");
        
        query.addCriteria(criteria);
        query.with(pageable);
        
        List<Candidate> candidates = mongoTemplate.find(query, Candidate.class);
        long total = mongoTemplate.count(query.skip(0).limit(0), Candidate.class);
        
        List<CandidateProfileDto> candidateDtos = candidates.stream()
                .map(this::convertToCandidateProfileDto)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(candidateDtos, pageable, total);
    }

    public Optional<CandidateProfileDto> getCandidateProfile(String candidateId) {
        Optional<Candidate> candidate = candidateRepository.findById(candidateId);
        if (candidate.isPresent()) {
            // Increment profile views
            Candidate c = candidate.get();
            c.setProfileViews(c.getProfileViews() != null ? c.getProfileViews() + 1 : 1);
            candidateRepository.save(c);
            
            return Optional.of(convertToCandidateProfileDto(c));
        }
        return Optional.empty();
    }

    public List<CandidateProfileDto> getRecentCandidates(int limit) {
        List<Candidate> candidates = candidateRepository.findTop10ByOrderByProfileCreatedDesc();
        return candidates.stream()
                .limit(limit)
                .map(this::convertToCandidateProfileDto)
                .collect(Collectors.toList());
    }

    public List<CandidateProfileDto> getActiveCandidates(int limit) {
        List<Candidate> candidates = candidateRepository.findTop10ByOrderByLastActiveDesc();
        return candidates.stream()
                .limit(limit)
                .map(this::convertToCandidateProfileDto)
                .collect(Collectors.toList());
    }

    public Candidate createCandidateFromResumeData(String email, String name, 
                                                  String phone, List<String> skills, 
                                                  List<Object> experience, List<Object> education) {
        Candidate candidate = new Candidate();
        candidate.setEmail(email);
        candidate.setName(name);
        candidate.setPhone(phone);
        candidate.setSkills(skills);
        candidate.setStatus("ACTIVE");
        candidate.setAvailabilityStatus("AVAILABLE");
        candidate.setProfileCreated(LocalDateTime.now());
        candidate.setLastActive(LocalDateTime.now());
        candidate.setProfileViews(0);
        
        // Set primary skills (first 5 skills)
        if (skills != null && !skills.isEmpty()) {
            candidate.setPrimarySkills(skills.stream().limit(5).collect(Collectors.toList()));
        }
        
        // Determine experience level based on skills and experience
        candidate.setExperienceLevel(determineExperienceLevel(skills, experience));
        
        return candidateRepository.save(candidate);
    }

    private CandidateProfileDto convertToCandidateProfileDto(Candidate candidate) {
        CandidateProfileDto dto = new CandidateProfileDto();
        dto.setId(candidate.getId());
        dto.setName(candidate.getName());
        dto.setEmail(candidate.getEmail());
        dto.setPhone(candidate.getPhone());
        dto.setLocation(candidate.getLocation());
        dto.setCurrentRole(candidate.getCurrentRole());
        dto.setDesiredRole(candidate.getDesiredRole());
        dto.setTotalExperienceYears(candidate.getTotalExperienceYears());
        dto.setExperienceLevel(candidate.getExperienceLevel());
        dto.setPrimarySkills(candidate.getPrimarySkills());
        dto.setSkillProficiency(candidate.getSkillProficiency());
        dto.setEducation(candidate.getEducation());
        dto.setWorkExperience(candidate.getWorkExperience());
        dto.setProfileSummary(candidate.getProfileSummary());
        dto.setAvailabilityStatus(candidate.getAvailabilityStatus());
        dto.setExpectedSalary(candidate.getExpectedSalary());
        dto.setPreferredLocation(candidate.getPreferredLocation());
        dto.setOpenToRemote(candidate.getOpenToRemote());
        dto.setNoticePeriod(candidate.getNoticePeriod());
        dto.setProfileViews(candidate.getProfileViews());
        dto.setLastActive(candidate.getLastActive());
        dto.setProfileCreated(candidate.getProfileCreated());
        dto.setStatus(candidate.getStatus());
        dto.setTags(candidate.getTags());
        
        // Additional calculated fields
        dto.setHasResume(candidate.getResumeFileId() != null);
        dto.setInterviewCount(interviewRepository.findByCandidateId(candidate.getId()).size());
        
        return dto;
    }

    private Pageable createPageable(CandidateSearchDto searchDto) {
        Sort.Direction direction = searchDto.getSortDirection().equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, searchDto.getSortBy());
        return PageRequest.of(searchDto.getPage(), searchDto.getSize(), sort);
    }

    private String determineExperienceLevel(List<String> skills, List<Object> experience) {
        if (experience == null || experience.isEmpty()) {
            return "FRESHER";
        }
        
        int expCount = experience.size();
        if (expCount <= 1) return "JUNIOR";
        if (expCount <= 3) return "MID";
        if (expCount <= 5) return "SENIOR";
        return "LEAD";
    }
}