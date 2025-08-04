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

public class FlashcardGenerator {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "google/gemini-2.0-flash-exp:free";
    private String apiKey;
    
    public FlashcardGenerator() {
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
    
    public CompletableFuture<List<Flashcard>> generateFlashcardsAsync(File documentFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Extract text from document
                String documentText = extractTextFromDocument(documentFile);
                
                // Truncate if too long
                if (documentText.length() > 15000) {
                    documentText = documentText.substring(0, 15000);
                }
                
                // Generate flashcards via API
                String result = callGeminiAPI(documentText);
                
                // Parse response into flashcards
                return parseFlashcardsFromResponse(result);
            } catch (Exception e) {
                System.err.println("Error generating flashcards: " + e.getMessage());
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
        request.setHeader("X-Title", "Zapio Flashcard Generator");
        
        // Build request JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        
        // Create prompt
        String prompt = "Based on the following document, create exactly 10 flashcards with key concepts. " +
                "Each flashcard should have a concise question on the front and a clear, informative answer on the back. " +
                "Format the output as a JSON array with the following structure for each flashcard: " +
                "{\"question\": \"Question text\", \"answer\": \"Answer text\"} " +
                "Here's the document:\n\n" + documentText;
        
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
    
    private List<Flashcard> parseFlashcardsFromResponse(String apiResponse) {
        List<Flashcard> flashcards = new ArrayList<>();
        
        try {
            // Try to extract JSON array from the response
            String jsonContent = apiResponse.trim();
            
            // If the response includes explanatory text before/after the JSON, extract just the JSON part
            int startBracket = jsonContent.indexOf('[');
            int endBracket = jsonContent.lastIndexOf(']');
            
            if (startBracket >= 0 && endBracket > startBracket) {
                jsonContent = jsonContent.substring(startBracket, endBracket + 1);
            }
            
            JSONArray flashcardsArray = new JSONArray(jsonContent);
            
            // Parse each flashcard
            for (int i = 0; i < flashcardsArray.length() && i < 10; i++) {
                JSONObject flashcardObj = flashcardsArray.getJSONObject(i);
                String question = flashcardObj.getString("question");
                String answer = flashcardObj.getString("answer");
                
                // Create and add the flashcard
                Flashcard flashcard = new Flashcard(question, answer);
                flashcards.add(flashcard);
            }
            
            // Ensure we have exactly 10 flashcards
            while (flashcards.size() < 10) {
                flashcards.add(new Flashcard(
                    "Important concept " + (flashcards.size() + 1),
                    "This is a placeholder for missing content."
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing API response: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: create some basic flashcards if parsing fails
            if (flashcards.isEmpty()) {
                for (int i = 1; i <= 10; i++) {
                    flashcards.add(new Flashcard(
                        "Key concept " + i,
                        "Failed to generate content. Please try again."
                    ));
                }
            }
        }
        
        return flashcards;
    }
}
