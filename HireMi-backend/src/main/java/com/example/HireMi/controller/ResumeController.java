package com.example.HireMi.controller;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.example.HireMi.dto.*;
import com.example.HireMi.mapper.ResumeMapper;
import com.example.HireMi.models.Resume;
import com.example.HireMi.service.ResumeParserService;
import com.example.HireMi.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ResumeController {

    @Autowired
    private ResumeParserService resumeParserService;
    @Autowired
    private ResumeService resumeService;
    @Autowired
    private ResumeMapper resumeMapper;

    @PostMapping("/api/resume/upload")
    public ResponseEntity<ApiResponseDto<ResumeUploadResponseDto>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("selectedRole") String selectedRole) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Please select a file to upload"));
            }

            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Only PDF files are allowed"));
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("File size should not exceed 10MB"));
            }

            Map<String, Object> resumeData = resumeParserService.parseResume(file);

            if (resumeData == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseDto.error("Failed to parse resume data"));
            }

            resumeData.put("role", selectedRole);
            ResumeUploadResponseDto responseDto = resumeMapper.toUploadResponseDto(resumeData);

            return ResponseEntity.ok(ApiResponseDto.success("Resume parsed successfully", responseDto));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to process PDF file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("api/resume/save")
    public ResponseEntity<ApiResponseDto<ResumeResponseDto>> saveResume(@Valid @RequestBody ResumeSaveDto resumeDto){
        try{
            Resume resume = resumeMapper.toEntity(resumeDto);
            Resume savedResume = resumeService.saveResume(resume);
            ResumeResponseDto responseDto = resumeMapper.toResponseDto(savedResume);

            return ResponseEntity.ok(ApiResponseDto.success("Resume saved successfully", responseDto));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to save resume: " + e.getMessage()));
        }
    }

    @GetMapping("api/resume/getResumeData")
    public ResponseEntity<ApiResponseDto<List<ResumeResponseDto>>> getResume(){
        List<Resume> resumeList = resumeService.getResume();
        if (resumeList == null || resumeList.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("No resumes found"));
        }

        List<ResumeResponseDto> responseDtos = resumeList.stream()
                .map(resumeMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok(ApiResponseDto.success("Resumes retrieved successfully", responseDtos));
    }

    @PostMapping("api/resume/getResumeByEmail")
    public ResponseEntity<ApiResponseDto<ResumeResponseDto>> getResumeByEmail(@Valid @RequestBody EmailRequestDto emailRequest){
        Resume resume = resumeService.getResumeByEmail(emailRequest.getEmail());
        if (resume == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponseDto.error("Resume not found for the provided email"));
        } else {
            ResumeResponseDto responseDto = resumeMapper.toResponseDto(resume);
            return ResponseEntity.ok(ApiResponseDto.success("Resume retrieved successfully", responseDto));
        }
    }
}
