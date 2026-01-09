package com.example.HireMi.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ResumeParserService {
    private final OpenRouterService openRouterService;

    @Autowired
    public ResumeParserService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public Map<String,Object> parseResume(MultipartFile file) throws IOException {
        PDDocument document = null;
        try {
            document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            return openRouterService.extractResumeData(text);

        } catch (Exception e) {
            throw new IOException("Failed to parse resume: " + e.getMessage(), e);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
}
