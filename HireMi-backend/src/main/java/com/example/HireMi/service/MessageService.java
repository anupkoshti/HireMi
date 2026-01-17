package com.example.HireMi.service;

import com.example.HireMi.dto.MessageDto;
import com.example.HireMi.models.Message;
import com.example.HireMi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(MessageDto messageDto) {
        Message message = new Message();
        message.setSenderId(messageDto.getSenderId());
        message.setSenderName(messageDto.getSenderName());
        message.setSenderEmail(messageDto.getSenderEmail());
        message.setSenderType(messageDto.getSenderType());
        message.setRecipientId(messageDto.getRecipientId());
        message.setRecipientName(messageDto.getRecipientName());
        message.setRecipientEmail(messageDto.getRecipientEmail());
        message.setRecipientType(messageDto.getRecipientType());
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        message.setMessageType(messageDto.getMessageType());
        message.setIsRead(false);
        message.setSentAt(LocalDateTime.now());
        
        // Generate thread ID if not provided
        if (messageDto.getThreadId() != null) {
            message.setThreadId(messageDto.getThreadId());
        } else {
            message.setThreadId(UUID.randomUUID().toString());
        }
        
        return messageRepository.save(message);
    }

    public Message sendInterviewInvite(String recruiterId, String recruiterName, String recruiterEmail,
                                     String candidateId, String candidateName, String candidateEmail,
                                     String jobRole, LocalDateTime interviewDateTime, String meetingLink) {
        MessageDto messageDto = new MessageDto();
        messageDto.setSenderId(recruiterId);
        messageDto.setSenderName(recruiterName);
        messageDto.setSenderEmail(recruiterEmail);
        messageDto.setSenderType("RECRUITER");
        messageDto.setRecipientId(candidateId);
        messageDto.setRecipientName(candidateName);
        messageDto.setRecipientEmail(candidateEmail);
        messageDto.setRecipientType("CANDIDATE");
        messageDto.setSubject("Interview Invitation - " + jobRole);
        messageDto.setContent(buildInterviewInviteContent(candidateName, jobRole, interviewDateTime, meetingLink));
        messageDto.setMessageType("INTERVIEW_INVITE");
        
        return sendMessage(messageDto);
    }

    public Message sendFollowUpMessage(String recruiterId, String recruiterName, String recruiterEmail,
                                     String candidateId, String candidateName, String candidateEmail,
                                     String content, String threadId) {
        MessageDto messageDto = new MessageDto();
        messageDto.setSenderId(recruiterId);
        messageDto.setSenderName(recruiterName);
        messageDto.setSenderEmail(recruiterEmail);
        messageDto.setSenderType("RECRUITER");
        messageDto.setRecipientId(candidateId);
        messageDto.setRecipientName(candidateName);
        messageDto.setRecipientEmail(candidateEmail);
        messageDto.setRecipientType("CANDIDATE");
        messageDto.setSubject("Follow-up");
        messageDto.setContent(content);
        messageDto.setMessageType("FOLLOW_UP");
        messageDto.setThreadId(threadId);
        
        return sendMessage(messageDto);
    }

    public Page<Message> getMessagesByRecipient(String recipientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        return messageRepository.findByRecipientId(recipientId, pageable);
    }

    public Page<Message> getMessagesBySender(String senderId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        return messageRepository.findBySenderId(senderId, pageable);
    }

    public List<Message> getMessageThread(String threadId) {
        return messageRepository.findByThreadIdOrderBySentAtAsc(threadId);
    }

    public List<Message> getUnreadMessages(String recipientId) {
        return messageRepository.findByRecipientIdAndIsReadFalse(recipientId);
    }

    public boolean markAsRead(String messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
            return true;
        }
        return false;
    }

    public boolean markThreadAsRead(String threadId, String recipientId) {
        List<Message> messages = messageRepository.findByThreadIdAndRecipientIdAndIsReadFalse(threadId, recipientId);
        for (Message message : messages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        }
        messageRepository.saveAll(messages);
        return true;
    }

    public long getUnreadCount(String recipientId) {
        return messageRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    private String buildInterviewInviteContent(String candidateName, String jobRole, 
                                             LocalDateTime interviewDateTime, String meetingLink) {
        return String.format(
            "Dear %s,\n\n" +
            "We are pleased to invite you for an interview for the %s position.\n\n" +
            "Interview Details:\n" +
            "Date & Time: %s\n" +
            "Meeting Link: %s\n\n" +
            "Please confirm your availability by replying to this message.\n\n" +
            "Best regards,\n" +
            "HireMi Recruitment Team",
            candidateName, jobRole, interviewDateTime.toString(), meetingLink
        );
    }

    public MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setSenderName(message.getSenderName());
        dto.setSenderEmail(message.getSenderEmail());
        dto.setSenderType(message.getSenderType());
        dto.setRecipientId(message.getRecipientId());
        dto.setRecipientName(message.getRecipientName());
        dto.setRecipientEmail(message.getRecipientEmail());
        dto.setRecipientType(message.getRecipientType());
        dto.setSubject(message.getSubject());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setIsRead(message.getIsRead());
        dto.setSentAt(message.getSentAt());
        dto.setReadAt(message.getReadAt());
        dto.setThreadId(message.getThreadId());
        return dto;
    }
}