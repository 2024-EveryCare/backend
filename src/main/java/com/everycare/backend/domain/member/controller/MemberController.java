package com.everycare.backend.domain.member.controller;

import com.everycare.backend.domain.member.dto.*;
import com.everycare.backend.domain.member.service.MemberService;
import com.everycare.backend.global.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.everycare.backend.global.common.ErrorCode.*;
import static com.everycare.backend.global.common.SuccessCode.*;

@RestController
@Tag(name = "Member API", description = "회원가입, 로그인 API ")
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;  // AuthenticationManager 주입

    @PostMapping(value = "/signup", produces = "application/json")
    @Operation(summary = "회원가입 API", description = "ID:이메일 형식, PW, 이름, 성별, 생년월일을 받습니다.")
    public ResponseEntity<RestApiResponse> registerUser(@RequestBody SignupRequest signupRequest) {
        memberService.createMember(signupRequest);
        return ResponseEntity.ok(RestApiResponse.of(MEMBER_SIGNUP_SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<RestApiResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // CustomUserDetails에서 member_id와 권한(ROLE) 콘솔 출력
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                Long memberId = userDetails.getMemberId();
                String email = userDetails.getUsername();
                logger.info("User ID: " + memberId);
                logger.info("Email: " + email);
                userDetails.getAuthorities().forEach(authority -> {
                    logger.info("User Role: " + authority.getAuthority());
                });
            } else {
                logger.info("Principal: " + principal.toString());
            }

            // 로그인 성공 시 리다이렉트 URL 설정
            // !!!!!!!!!! 임시로 설정!!! 세션 확인용임
            RestApiResponse response = RestApiResponse.of(LOGIN_SUCCESS);
            response.setData("/api/v1/medicines/photoUpload"); // 데이터를 설정하는 부분

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestApiResponse.of(INVALID_PASSWORD));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestApiResponse.of(EMAIL_NOT_FOUND));
        }
    }
}
