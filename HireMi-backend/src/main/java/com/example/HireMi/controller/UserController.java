package com.example.HireMi.controller;

import com.example.HireMi.dto.ApiResponseDto;
import com.example.HireMi.dto.LoginRequestDto;
import com.example.HireMi.dto.LoginResponseDto;
import com.example.HireMi.dto.UserRegistrationDto;
import com.example.HireMi.mapper.UserMapper;
import com.example.HireMi.service.UserService;
import com.example.HireMi.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;

    // Helper method to extract JWT token from Authorization header
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "";
    }

    @PostMapping("/users/register")
    public ResponseEntity<ApiResponseDto<?>> registerUser(@Valid @RequestBody UserRegistrationDto userDto){
        return userService.registerUser(userDto);
    }

    @PostMapping("/users/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        return userService.loginUser(loginRequest);
    }

//    @GetMapping("/users/profile")
//    public ResponseEntity<ApiResponseDto<UserProfileDto>> getUserProfile(@RequestHeader(name="Authorization", defaultValue = "") String authHeader) {
//        String jwt = extractToken(authHeader);
//        if(jwt.isEmpty()) {
//            return ResponseEntity.status(401).body(ApiResponseDto.error("Token is empty"));
//        }
//        if(!userService.validateToken(jwt)) {
//            return ResponseEntity.status(401).body(ApiResponseDto.error("Invalid token"));
//        }
//        String email = jwtUtil.extractemail(jwt);
//        Optional<User> user = userRepository.findById(email);
//        if(user.isEmpty()) {
//            return ResponseEntity.status(404).body(ApiResponseDto.error("User not found"));
//        }
//
//        UserProfileDto profileDto = userMapper.toProfileDto(user.get());
//        return ResponseEntity.ok(ApiResponseDto.success("Profile retrieved successfully", profileDto));
//    }

    @GetMapping("/api/ping")
    public ResponseEntity<ApiResponseDto<String>> ping() {
        return ResponseEntity.ok(ApiResponseDto.success("Pong", "Server is running"));
    }
}
