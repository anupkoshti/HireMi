package com.example.HireMi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    
    private String senderId;
    private String senderName;
    private String senderEmail;
    private String senderType; // RECRUITER, CANDIDATE
    
    private String recipientId;
    private String recipientName;
    private String recipientEmail;
    private String recipientType; // RECRUITER, CANDIDATE
    
    private String subject;
    private String content;
    private String messageType; // INTERVIEW_INVITE, FOLLOW_UP, REJECTION, OFFER, GENERAL
    
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    
    private String threadId; // For grouping related messages
}