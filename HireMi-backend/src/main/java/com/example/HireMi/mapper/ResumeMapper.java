package com.example.HireMi.mapper;


import com.example.HireMi.dto.ResumeResponseDto;
import com.example.HireMi.dto.ResumeSaveDto;
import com.example.HireMi.dto.ResumeUploadResponseDto;
import com.example.HireMi.models.Resume;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ResumeMapper {

    public Resume toEntity(ResumeSaveDto dto) {
        Resume resume = new Resume();
        resume.setName(dto.getName());
        resume.setEmail(dto.getEmail());
        resume.setPhone(dto.getPhone());
        resume.setSkills(dto.getSkills());
        resume.setExperience(dto.getExperience());
        resume.setEducation(dto.getEducation());
        resume.setRole(dto.getRole());
        return resume;
    }

    public ResumeResponseDto toResponseDto(Resume resume) {
        ResumeResponseDto dto = new ResumeResponseDto();
        dto.setId(resume.getEmail()); // Using email as ID
        dto.setName(resume.getName());
        dto.setEmail(resume.getEmail());
        dto.setPhone(resume.getPhone());
        dto.setSkills(resume.getSkills());
        dto.setExperience(resume.getExperience());
        dto.setEducation(resume.getEducation());
        dto.setRole(resume.getRole());
        return dto;
    }

    @SuppressWarnings("unchecked")
    public ResumeUploadResponseDto toUploadResponseDto(Map<String, Object> resumeData) {
        ResumeUploadResponseDto dto = new ResumeUploadResponseDto();
        dto.setName((String) resumeData.get("name"));
        dto.setEmail((String) resumeData.get("email"));
        dto.setPhone((String) resumeData.get("phone"));
        dto.setSkills((List<String>) resumeData.get("skills"));
        dto.setExperience((List<String>) resumeData.get("experience"));
        dto.setEducation((List<String>) resumeData.get("education"));
        dto.setRole((String) resumeData.get("role"));
        dto.setParseError((String) resumeData.get("parseError"));
        dto.setRawContent((String) resumeData.get("rawContent"));
        return dto;
    }
}
