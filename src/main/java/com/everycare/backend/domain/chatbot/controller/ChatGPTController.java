package com.everycare.backend.domain.chatbot.controller;

import com.everycare.backend.domain.chatbot.dto.ChatCompletionRequest;
import com.everycare.backend.domain.chatbot.dto.ChatCompletionResponse;
import com.everycare.backend.domain.chatbot.dto.ChatResponseDTO;
import com.everycare.backend.domain.chatbot.dto.PromptDTO;
import com.everycare.backend.global.common.RestApiResponse;
import com.everycare.backend.global.common.SuccessCode;
import com.everycare.backend.domain.chatbot.service.ChatSessionService;
import com.everycare.backend.domain.member.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private ChatSessionService chatSessionService;

    @PostMapping("/ask")
    @Operation(summary = "일반 채팅 API", description = "챗봇에게 질문할 때 사용")
    public RestApiResponse getOpenaiResponse(@RequestBody PromptDTO promptDTO) {
        // 현재 인증된 사용자의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId; // 스트링으로 지정.

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            memberId = userDetails.getMemberId().toString(); // memberId를 가져옴
        } else {
            // 인증되지 않은 사용자는 처리 로직 (예: 익명 사용자 등)
            memberId = "anonymous";
        }

        // Redis에서 이전 대화 기록 가져오기
        String previousChat = chatSessionService.getChatHistory(memberId);

        System.out.println("Previous Chat: " + previousChat);

        // 새로운 대화 내용을 추가하여 대화 기록 생성
        String combinedChat = (previousChat == null ? "" : previousChat + "\n") + promptDTO.getPrompt();

        // OpenAI에 보낼 요청 생성
        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", combinedChat);

        // OpenAI API 호출
        ChatCompletionResponse response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                chatCompletionRequest,
                ChatCompletionResponse.class
        );

        // 응답에서 내용을 추출
        String content = response.getChoices().get(0).getMessage().getContent();

        // Redis에 대화 기록 업데이트
        chatSessionService.saveChatHistory(memberId, combinedChat + "\nAssistant: " + content);

        // API 응답 포맷으로 반환
        return RestApiResponse.of(SuccessCode.CHATGPT_RESPONSE_SUCCESS, new ChatResponseDTO(content));
    }
}
