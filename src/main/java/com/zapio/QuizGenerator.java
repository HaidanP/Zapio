package com.zapio;

import io.github.cdimascio.dotenv.Dotenv;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuizGenerator {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "google/gemini-2.0-flash-exp:free";
    private String apiKey;
    
    public QuizGenerator() {
        // Load API key from .env file
        try {
            Dotenv dotenv = Dotenv.configure().load();
            apiKey = dotenv.get("OPENROUTER_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("OPENROUTER_API_KEY not found in .env file");
            }
        } catch (Exception e) {
            System.err.println("Error loading API key from .env file: " + e.getMessage());
            throw new RuntimeException("Failed to load API key. Please ensure the .env file exists with OPENROUTER_API_KEY defined.", e);
        }
    }
    
    public CompletableFuture<List<QuizQuestion>> generateQuestionsAsync(File documentFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Extract text from document
                String documentText = extractTextFromDocument(documentFile);
                
                // Truncate if too long
                if (documentText.length() > 15000) {
                    documentText = documentText.substring(0, 15000);
                }
                
                // Generate questions via API
                String result = callGeminiAPI(documentText);
                
                // Parse response into questions
                return parseQuestionsFromResponse(result);
            } catch (Exception e) {
                System.err.println("Error generating questions: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
    
    private String extractTextFromDocument(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        
        if (fileName.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else if (fileName.endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                return extractor.getText();
            }
        } else if (fileName.endsWith(".txt")) {
            return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
        } else {
            throw new IOException("Unsupported file format: " + fileName);
        }
    }
    
    private String callGeminiAPI(String documentText) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(API_URL);
        
        // Set headers
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + apiKey);
        request.setHeader("HTTP-Referer", "http://localhost:8080");
        request.setHeader("X-Title", "Zapio Quiz Generator");
        
        // Build request JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        
        // Create prompt
        String prompt = "Based on the following document, create a quiz with 10 single-choice questions. " +
                "For each question, provide exactly 4 options where only ONE is correct. " +
                "Format the output as a JSON array with the following structure for each question: " +
                "{\"question\": \"Question text\", \"options\": [\"option1\", \"option2\", \"option3\", \"option4\"], \"correctOption\": 0} " +
                "where correctOption is the index (0-3) of the correct answer. Here's the document:\n\n" + documentText;
        
        JSONArray content = new JSONArray();
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        content.put(textContent);
        
        message.put("content", content);
        messages.put(message);
        requestBody.put("messages", messages);
        
        // Set request entity
        request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
        
        // Execute request
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                JSONObject jsonResponse = new JSONObject(result);
                
                // Extract the model's response from the JSON
                String generatedContent = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
                
                return generatedContent;
            }
        }
        
        throw new IOException("Failed to get a valid response from the API");
    }
    
    private List<QuizQuestion> parseQuestionsFromResponse(String apiResponse) {
        List<QuizQuestion> questions = new ArrayList<>();
        
        try {
            // Try to extract JSON array from the response
            // First, try to find array directly
            String jsonContent = apiResponse.trim();
            
            // If the response includes explanatory text before/after the JSON, extract just the JSON part
            int startBracket = jsonContent.indexOf('[');
            int endBracket = jsonContent.lastIndexOf(']');
            
            if (startBracket >= 0 && endBracket > startBracket) {
                jsonContent = jsonContent.substring(startBracket, endBracket + 1);
            }
            
            JSONArray questionsArray = new JSONArray(jsonContent);
            
            // Parse each question
            for (int i = 0; i < questionsArray.length() && i < 10; i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);
                String questionText = questionObj.getString("question");
                
                JSONArray optionsArray = questionObj.getJSONArray("options");
                List<String> options = new ArrayList<>();
                
                for (int j = 0; j < optionsArray.length() && j < 4; j++) {
                    options.add(optionsArray.getString(j));
                }
                
                // Ensure we have exactly 4 options
                while (options.size() < 4) {
                    options.add("N/A");
                }
                
                int correctOption = questionObj.getInt("correctOption");
                
                // Create and add the question
                QuizQuestion quizQuestion = new QuizQuestion(questionText, options, correctOption);
                questions.add(quizQuestion);
            }
        } catch (Exception e) {
            System.err.println("Error parsing API response: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: create some basic questions if parsing fails
            if (questions.isEmpty()) {
                List<String> defaultOptions = List.of("Option 1", "Option 2", "Option 3", "Option 4");
                questions.add(new QuizQuestion("Failed to parse API response. Question 1?", defaultOptions));
                questions.add(new QuizQuestion("Failed to parse API response. Question 2?", defaultOptions));
            }
        }
        
        return questions;
    }
}
