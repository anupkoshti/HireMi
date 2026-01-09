package com.example.HireMi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponseDto {
    private String id;
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private List<String> experience;
    private List<String> education;
    private String role;
}
