package com.example.HireMi.mapper;

import com.example.HireMi.dto.LoginResponseDto;
import com.example.HireMi.dto.UserRegistrationDto;
import com.example.HireMi.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegistrationDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Will be encoded in service
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setLocation(dto.getLocation());
        user.setBio(dto.getBio());
        user.setExperience(dto.getExperience());
        return user;
    }

//    public UserProfileDto toProfileDto(User user) {
//        UserProfileDto dto = new UserProfileDto();
//        dto.setEmail(user.getEmail());
//        dto.setName(user.getName());
//        dto.setPhone(user.getPhone());
//        dto.setLocation(user.getLocation());
//        dto.setBio(user.getBio());
//        dto.setExperience(user.getExperience());
//        return dto;
//    }

    public LoginResponseDto toLoginResponseDto(String message, String token, User user, long expiresIn) {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setMessage(message);
        dto.setToken(token);
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setExpiresIn(expiresIn);
        return dto;
    }
}
