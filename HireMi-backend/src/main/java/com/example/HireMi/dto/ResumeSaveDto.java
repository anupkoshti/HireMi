package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeSaveDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Skills are required")
    private List<String> skills;

    @NotNull(message = "Experience is required")
    private List<String> experience;

    @NotNull(message = "Education is required")
    private List<String> education;

    private String role;
}
