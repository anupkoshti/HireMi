package com.example.HireMi.service;

import java.util.List;

import com.example.HireMi.models.Resume;
import com.example.HireMi.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;

    public Resume saveResume(Resume resume){
        return resumeRepository.save(resume);
    }
    public List<Resume> getResume(){
        return resumeRepository.findAll();
    }
    public Resume getResumeByEmail(String email) {
        Resume resume = resumeRepository.findById(email).orElse(null);
        System.out.println("Resume fetched: " + resume);
        return resumeRepository.findById(email).orElse(null);
    }

}

