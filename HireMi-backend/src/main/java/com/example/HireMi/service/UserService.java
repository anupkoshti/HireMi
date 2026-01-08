package com.example.HireMi.service;

import com.example.HireMi.dto.ApiResponseDto;
import com.example.HireMi.dto.LoginRequestDto;
import com.example.HireMi.dto.LoginResponseDto;
import com.example.HireMi.dto.UserRegistrationDto;
import com.example.HireMi.mapper.UserMapper;
import com.example.HireMi.models.User;
import com.example.HireMi.repository.UserRepository;
import com.example.HireMi.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;

    public ResponseEntity<ApiResponseDto<?>> registerUser(UserRegistrationDto userDto){
        // Normalize email to lowercase
        String normalizedEmail = userDto.getEmail().trim().toLowerCase();

        if (userRepository.existsById(normalizedEmail)) {
            return ResponseEntity.status(403).body(ApiResponseDto.error("User already exists"));
        }

        // Convert DTO to entity
        User user = userMapper.toEntity(userDto);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponseDto.success("User registered successfully"));
    }

    public ResponseEntity<ApiResponseDto<LoginResponseDto>> loginUser(LoginRequestDto loginDto) {
        // Normalize email to lowercase
        String normalizedEmail = loginDto.getEmail().trim().toLowerCase();

        Optional<User> users = userRepository.findByEmail(normalizedEmail);
        if(users.isEmpty()){
            return ResponseEntity.status(404).body(ApiResponseDto.error("User not found"));
        }

        User user = users.get();
        if(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            // Generate JWT token
            String token = jwtUtil.generateToken(normalizedEmail);
            long expiresIn = jwtUtil.getExpirationTime();

            LoginResponseDto responseDto = userMapper.toLoginResponseDto(
                    "Login successful", token, user, expiresIn);

            return ResponseEntity.ok(ApiResponseDto.success("Login successful", responseDto));
        }
        return ResponseEntity.status(401).body(ApiResponseDto.error("Invalid credentials"));
    }

    public boolean validateToken(String token){
        return jwtUtil.validateToken(token, jwtUtil.extractemail(token));
    }
}
