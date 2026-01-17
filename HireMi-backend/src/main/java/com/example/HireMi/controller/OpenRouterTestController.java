package com.example.HireMi.controller;

import com.example.HireMi.models.Candidate;
import com.example.HireMi.service.CandidateService;
import com.example.HireMi.service.OpenRouterService;
import com.example.HireMi.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class OpenRouterTestController {

    @Autowired
    private OpenRouterService openRouterService;

    @Autowired
    private ResumeParserService resumeParserService;

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/openrouter")
    public ResponseEntity<Map<String, Object>> testOpenRouterConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = openRouterService.testConnection();
            
            response.put("status", "SUCCESS");
            response.put("message", "OpenRouter API connection successful");
            response.put("response", result);
            response.put("model", "xiaomi/mimo-v2-flash:free");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "OpenRouter API connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/openrouter/resume")
    public ResponseEntity<Map<String, Object>> testOpenRouterResumeParser() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test with a sample resume text
            String testResumeText = """
                Sarah Johnson
                Email: sarah.johnson@email.com
                Phone: (555) 987-6543
                
                Skills: Python, Django, PostgreSQL, React, JavaScript, Docker, AWS
                
                Experience:
                - Senior Full Stack Developer at TechStart Inc (2022-2024)
                  Led development of web applications using Django and React
                  Managed cloud infrastructure on AWS with Docker containers
                  Mentored junior developers and conducted code reviews
                  
                - Full Stack Developer at WebSolutions Co (2019-2022)
                  Built REST APIs using Python and Django framework
                  Developed responsive frontend applications with React
                  Implemented CI/CD pipelines and automated testing
                
                Education:
                - Master of Science in Computer Science, Stanford University (2019)
                - Bachelor of Science in Software Engineering, UC Berkeley (2017)
                """;
            
            Map<String, Object> result = openRouterService.extractResumeData(testResumeText);
            
            response.put("status", "SUCCESS");
            response.put("message", "OpenRouter resume parsing successful");
            response.put("data", result);
            response.put("model", "xiaomi/mimo-v2-flash:free");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "OpenRouter resume parsing failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/openrouter/custom")
    public ResponseEntity<Map<String, Object>> testCustomText(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String resumeText = request.get("text");
            if (resumeText == null || resumeText.trim().isEmpty()) {
                response.put("status", "ERROR");
                response.put("message", "Text is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> result = openRouterService.extractResumeData(resumeText);
            
            response.put("status", "SUCCESS");
            response.put("message", "OpenRouter processing successful");
            response.put("data", result);
            response.put("model", "xiaomi/mimo-v2-flash:free");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "OpenRouter processing failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/openrouter/pdf")
    public ResponseEntity<Map<String, Object>> testPdfUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("status", "ERROR");
                response.put("message", "Please select a PDF file to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if it's a PDF file
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                response.put("status", "ERROR");
                response.put("message", "Please upload a PDF file only");
                response.put("receivedContentType", contentType);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Parse the PDF using the ResumeParserService
            Map<String, Object> result = resumeParserService.parseResume(file);
            
            response.put("status", "SUCCESS");
            response.put("message", "PDF resume parsing successful");
            response.put("data", result);
            response.put("model", "xiaomi/mimo-v2-flash:free");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize() + " bytes");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "PDF parsing failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("fileName", file.getOriginalFilename());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/openrouter/pdf/create-candidate")
    public ResponseEntity<Map<String, Object>> uploadResumeAndCreateCandidate(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("status", "ERROR");
                response.put("message", "Please select a PDF file to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if it's a PDF file
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                response.put("status", "ERROR");
                response.put("message", "Please upload a PDF file only");
                response.put("receivedContentType", contentType);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Parse the PDF using the ResumeParserService
            Map<String, Object> parsedData = resumeParserService.parseResume(file);
            
            // Extract candidate information
            String email = (String) parsedData.get("email");
            String name = (String) parsedData.get("name");
            String phone = (String) parsedData.get("phone");
            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) parsedData.get("skills");
            @SuppressWarnings("unchecked")
            List<Object> experience = (List<Object>) parsedData.get("experience");
            @SuppressWarnings("unchecked")
            List<Object> education = (List<Object>) parsedData.get("education");
            
            // Create candidate profile
            Candidate candidate = candidateService.createCandidateFromResumeData(
                email, name, phone, skills, experience, education
            );
            
            response.put("status", "SUCCESS");
            response.put("message", "Resume parsed and candidate profile created successfully");
            response.put("candidateId", candidate.getId());
            response.put("candidateName", candidate.getName());
            response.put("candidateEmail", candidate.getEmail());
            response.put("parsedData", parsedData);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize() + " bytes");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to create candidate profile: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("fileName", file.getOriginalFilename());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}