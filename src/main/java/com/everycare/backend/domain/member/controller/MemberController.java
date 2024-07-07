package com.everycare.backend.domain.member.controller;

import com.everycare.backend.domain.member.dto.*;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.service.MemberService;
import com.everycare.backend.global.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

import static com.everycare.backend.global.common.ErrorCode.*;
import static com.everycare.backend.global.common.SuccessCode.*;

@RestController
@Tag(name = "Example", description = "Example API")
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", produces = "application/json")
    @Operation(summary = "회원가입 API", description = "ID:이메일 형식, PW, 이름, 성별, 생년월일을 받습니다.")
    public ResponseEntity<RestApiResponse> registerUser(@RequestBody SignupRequest signupRequest) {
        memberService.createMember(signupRequest);
        return ResponseEntity.ok(RestApiResponse.of(MEMBER_SIGNUP_SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<RestApiResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<Member> optionalMember = memberService.findByEmail(loginRequest.getEmail());

        if (optionalMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestApiResponse.of(EMAIL_NOT_FOUND));
        }

        Member member = optionalMember.get();

        if (memberService.checkPassword(Optional.of(member), loginRequest.getPassword())) {
            return ResponseEntity.ok(RestApiResponse.of(LOGIN_SUCCESS));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestApiResponse.of(INVALID_PASSWORD));
        }
    }

}