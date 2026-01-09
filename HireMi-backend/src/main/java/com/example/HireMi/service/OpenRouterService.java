package com.example.HireMi.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenRouterService {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL_NAME = "xiaomi/mimo-v2-flash:free";

    @Value("${OPENROUTER_API_KEY}")
    private String apiKey;

    public Map<String, Object> extractResumeData(String resumeText) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        // Create the request body for OpenRouter API (OpenAI-compatible format)
        JSONObject body = new JSONObject();
        body.put("model", MODEL_NAME);
        body.put("temperature", 0.3);
        body.put("max_tokens", 4000);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", 
            "You are a resume parser that extracts structured data from resumes. " +
            "Return ONLY valid JSON with no additional text or formatting. " +
            "Do not include markdown code blocks or any other text."));
        
        String userPrompt = """
            Parse this resume and return ONLY valid JSON with this exact structure:
            {
              "name": "Full Name",
              "email": "email@example.com", 
              "phone": "phone number",
              "skills": ["skill1", "skill2", "skill3"],
              "experience": [
                {
                  "title": "Job Title",
                  "company": "Company Name", 
                  "duration": "Start-End Date",
                  "description": "Job description"
                }
              ],
              "education": [
                {
                  "degree": "Degree Name",
                  "institution": "School Name",
                  "year": "Graduation Year"
                }
              ]
            }
            
            Resume text:
            """ + resumeText;
            
        messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
        body.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "http://localhost:8080") // Optional: for analytics
                .header("X-Title", "HireMi Resume Parser") // Optional: for analytics
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API request failed with status code: " + response.statusCode() + 
                              ". Response: " + response.body());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        
        // Extract content from OpenRouter response (OpenAI-compatible format)
        String content = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // Clean up the response text (remove any markdown formatting if present)
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();

        JSONObject data = new JSONObject(content);
        Map<String, Object> result = new HashMap<>();

        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value instanceof JSONArray) {
                result.put(key, ((JSONArray)value).toList());
            } else {
                result.put(key, value);
            }
        }
        
        return result;
    }

    public String testConnection() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        JSONObject body = new JSONObject();
        body.put("model", MODEL_NAME);
        body.put("temperature", 0.7);
        body.put("max_tokens", 100);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", 
            "Say 'Hello from OpenRouter API!' to test the connection. Keep it short."));
        body.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "http://localhost:8080")
                .header("X-Title", "HireMi Connection Test")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API request failed with status code: " + response.statusCode() + 
                              ". Response: " + response.body());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}