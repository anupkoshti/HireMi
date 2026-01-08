package com.example.HireMi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String message;
    private String token;
    private String email;
    private String name;
    private long expiresIn;
}
