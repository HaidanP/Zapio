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
import java.util.concurrent.CompletableFuture;

/**
 * Generator for comprehensive cheatsheets using Gemini API
 */
public class CheatsheetGenerator {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "google/gemini-2.0-flash-exp:free";
    private String apiKey;
    
    public CheatsheetGenerator() {
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
    
    /**
     * Generate a cheatsheet asynchronously from the provided document
     * @param documentFile File to generate cheatsheet from
     * @return CompletableFuture containing the generated cheatsheet text
     */
    public CompletableFuture<String> generateCheatsheetAsync(File documentFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Extract text from document
                String documentText = extractTextFromDocument(documentFile);
                
                // Truncate if too long
                if (documentText.length() > 15000) {
                    documentText = documentText.substring(0, 15000);
                }
                
                // Generate cheatsheet via API
                return callGeminiAPI(documentText);
            } catch (Exception e) {
                System.err.println("Error generating cheatsheet: " + e.getMessage());
                e.printStackTrace();
                return "Error generating cheatsheet. Please try again.";
            }
        });
    }
    
    /**
     * Extract text from various document formats
     */
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
    
    /**
     * Call the Gemini API to generate a cheatsheet
     */
    private String callGeminiAPI(String documentText) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(API_URL);
        
        // Set headers
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + apiKey);
        request.setHeader("HTTP-Referer", "http://localhost:8080");
        request.setHeader("X-Title", "Zapio Cheatsheet Generator");
        
        // Build request JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        
        // Create prompt
        String prompt = "Create a comprehensive, well-structured cheatsheet based on the following document. " +
                "IMPORTANT: Return the response in plain text only without any special characters or formatting. " +
                "Format requirements: " +
                "1. DO NOT use any markdown formatting " +
                "2. DO NOT use hashtags (#) for headings " +
                "3. DO NOT use asterisks (*) or hyphens (-) for bullet points " +
                "4. DO NOT use underscores, backticks, or any other special characters " +
                "5. Simply use numbers and letters for sections (e.g. '1.', 'a.', etc.) " +
                "6. Use all CAPS for main section titles " +
                "7. Use Title Case for subsection titles " +
                "8. Leave a blank line between sections " +
                "Include all key concepts, definitions, formulas, and critical information. " +
                "Make it visually scannable with consistent organization using only plain text. " +
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
}
