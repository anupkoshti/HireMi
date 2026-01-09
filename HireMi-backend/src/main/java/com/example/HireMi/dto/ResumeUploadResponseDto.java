package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadResponseDto {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private List<String> experience;
    private List<String> education;
    private String role;
    private String parseError; // In case of parsing issues
    private String rawContent; // Truncated raw content for debugging
}
