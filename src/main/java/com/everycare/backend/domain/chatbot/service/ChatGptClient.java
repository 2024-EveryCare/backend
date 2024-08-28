package com.everycare.backend.domain.chatbot.service;

import com.everycare.backend.domain.chatbot.dto.ChatCompletionRequest;
import com.everycare.backend.domain.chatbot.dto.ChatCompletionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptClient {

    private final RestTemplate restTemplate;

    @Value("${OPENAI_API_URL}")
    private String apiUrl;

    public ChatGptClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getChatGptResponse(String prompt) {
        // OpenAI에 보낼 요청 생성
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest("gpt-4", prompt);

        // Create an HTTP entity with the request body (headers are managed by the RestTemplate's interceptor)
        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(chatCompletionRequest);

        // OpenAI API 호출
        ResponseEntity<ChatCompletionResponse> response = restTemplate.postForEntity(
                apiUrl,
                entity,
                ChatCompletionResponse.class
        );

        // 응답에서 내용을 추출
        String content = response.getBody().getChoices().get(0).getMessage().getContent();

        // 결과 반환
        return content;
    }
}
