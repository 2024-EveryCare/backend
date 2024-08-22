package com.everycare.backend.domain.chatbot.controller;

import com.everycare.backend.domain.chatbot.dto.ChatCompletionRequest;
import com.everycare.backend.domain.chatbot.dto.ChatCompletionResponse;
import com.everycare.backend.domain.chatbot.dto.ChatResponseDTO;
import com.everycare.backend.domain.chatbot.dto.PromptDTO;
import com.everycare.backend.global.common.RestApiResponse;
import com.everycare.backend.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Tag(name = "ChatGPT API", description = "챗GPT API - 일반 챗봇 / 복용 내역 모니터링")
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatGPTController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/ask")
    @Operation(summary = "일반 채팅 API", description = "챗봇에게 질문할 때 사용")
    public RestApiResponse getOpenaiResponse(@RequestBody PromptDTO promptDTO) {
        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", promptDTO.getPrompt());

        ChatCompletionResponse response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                chatCompletionRequest,
                ChatCompletionResponse.class
        );

        // Extract the content from the response
        String content = response.getChoices().get(0).getMessage().getContent();

        // Create a structured response
        return RestApiResponse.of(SuccessCode.CHATGPT_RESPONSE_SUCCESS, new ChatResponseDTO(content));
    }
}
