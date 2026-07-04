package com.eduardo.AdminBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GrokService {

    @Value("${groq.api-key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String perguntar(String systemPrompt, String userMessage) {
        return perguntarComHistorico(systemPrompt, List.of(Map.of("role", "user", "content", userMessage)));
    }

    public String perguntarComHistorico(String systemPrompt, List<Map<String, String>> mensagens) {
        try {
            List<Map<String, Object>> messages = new ArrayList<>();

            if (systemPrompt != null && !systemPrompt.isBlank()) {
                messages.add(Map.of("role", "system", "content", systemPrompt));
            }

            for (Map<String, String> msg : mensagens) {
                messages.add(Map.of("role", msg.get("role"), "content", msg.get("content")));
            }

            Map<String, Object> bodyMap = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", messages,
                    "max_tokens", 1024
            );

            String json = objectMapper.writeValueAsString(bodyMap);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);
                return root.get("choices").get(0).get("message").get("content").asText();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Erro na IA: " + e.getMessage();
        }
    }
}