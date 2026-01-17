package com.example.HireMi.service;

import com.example.HireMi.dto.InterviewDto;
import com.example.HireMi.models.Interview;
import com.example.HireMi.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    public Interview scheduleInterview(InterviewDto interviewDto) {
        Interview interview = new Interview();
        interview.setCandidateId(interviewDto.getCandidateId());
        interview.setCandidateName(interviewDto.getCandidateName());
        interview.setCandidateEmail(interviewDto.getCandidateEmail());
        interview.setRecruiterId(interviewDto.getRecruiterId());
        interview.setRecruiterName(interviewDto.getRecruiterName());
        interview.setRecruiterEmail(interviewDto.getRecruiterEmail());
        interview.setJobRole(interviewDto.getJobRole());
        interview.setInterviewType(interviewDto.getInterviewType());
        interview.setScheduledDateTime(interviewDto.getScheduledDateTime());
        interview.setDurationMinutes(interviewDto.getDurationMinutes());
        interview.setMeetingLink(interviewDto.getMeetingLink());
        interview.setLocation(interviewDto.getLocation());
        interview.setNotes(interviewDto.getNotes());
        interview.setStatus("SCHEDULED");
        interview.setCreatedAt(LocalDateTime.now());
        interview.setUpdatedAt(LocalDateTime.now());
        
        return interviewRepository.save(interview);
    }

    public Optional<Interview> updateInterviewStatus(String interviewId, String status, String feedback, Integer rating, String decision) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isPresent()) {
            Interview interview = interviewOpt.get();
            interview.setStatus(status);
            if (feedback != null) interview.setFeedback(feedback);
            if (rating != null) interview.setRating(rating);
            if (decision != null) interview.setDecision(decision);
            interview.setUpdatedAt(LocalDateTime.now());
            
            return Optional.of(interviewRepository.save(interview));
        }
        return Optional.empty();
    }

    public Page<Interview> getInterviewsByCandidate(String candidateId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scheduledDateTime"));
        return interviewRepository.findByCandidateId(candidateId, pageable);
    }

    public Page<Interview> getInterviewsByRecruiter(String recruiterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scheduledDateTime"));
        return interviewRepository.findByRecruiterId(recruiterId, pageable);
    }

    public List<Interview> getUpcomingInterviews(String recruiterId, int days) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(days);
        return interviewRepository.findByRecruiterIdAndScheduledDateTimeBetweenAndStatus(
            recruiterId, startDate, endDate, "SCHEDULED");
    }

    public List<Interview> getTodaysInterviews(String recruiterId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return interviewRepository.findByRecruiterIdAndScheduledDateTimeBetween(
            recruiterId, startOfDay, endOfDay);
    }

    public boolean rescheduleInterview(String interviewId, LocalDateTime newDateTime) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isPresent()) {
            Interview interview = interviewOpt.get();
            interview.setScheduledDateTime(newDateTime);
            interview.setStatus("RESCHEDULED");
            interview.setUpdatedAt(LocalDateTime.now());
            interviewRepository.save(interview);
            return true;
        }
        return false;
    }

    public boolean cancelInterview(String interviewId, String reason) {
        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isPresent()) {
            Interview interview = interviewOpt.get();
            interview.setStatus("CANCELLED");
            interview.setNotes(interview.getNotes() + "\nCancellation reason: " + reason);
            interview.setUpdatedAt(LocalDateTime.now());
            interviewRepository.save(interview);
            return true;
        }
        return false;
    }

    public InterviewDto convertToDto(Interview interview) {
        InterviewDto dto = new InterviewDto();
        dto.setId(interview.getId());
        dto.setCandidateId(interview.getCandidateId());
        dto.setCandidateName(interview.getCandidateName());
        dto.setCandidateEmail(interview.getCandidateEmail());
        dto.setRecruiterId(interview.getRecruiterId());
        dto.setRecruiterName(interview.getRecruiterName());
        dto.setRecruiterEmail(interview.getRecruiterEmail());
        dto.setJobRole(interview.getJobRole());
        dto.setInterviewType(interview.getInterviewType());
        dto.setStatus(interview.getStatus());
        dto.setScheduledDateTime(interview.getScheduledDateTime());
        dto.setDurationMinutes(interview.getDurationMinutes());
        dto.setMeetingLink(interview.getMeetingLink());
        dto.setLocation(interview.getLocation());
        dto.setFeedback(interview.getFeedback());
        dto.setRating(interview.getRating());
        dto.setDecision(interview.getDecision());
        dto.setNotes(interview.getNotes());
        dto.setCreatedAt(interview.getCreatedAt());
        dto.setUpdatedAt(interview.getUpdatedAt());
        return dto;
    }
}