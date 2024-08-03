package com.everycare.backend.domain.member.controller;

import com.everycare.backend.domain.member.dto.*;
import com.everycare.backend.domain.member.service.MemberService;
import com.everycare.backend.global.common.RestApiResponse;
import com.everycare.backend.global.common.SuccessCode;
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
@Tag(name = "Member API", description = "회원가입, 로그인 API")
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

            RestApiResponse response = RestApiResponse.of(LOGIN_SUCCESS);
            response.setData("/api/v1/medicines/photoUpload");

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestApiResponse.of(INVALID_PASSWORD));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestApiResponse.of(EMAIL_NOT_FOUND));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    @Operation(summary = "로그아웃 API", description = "로그아웃 요청을 처리합니다.")
    public ResponseEntity<RestApiResponse> logoutUser() {
        logger.info("Logout request received");
        try {
            SecurityContextHolder.clearContext();
            logger.info("Logout successful");
            return ResponseEntity.ok(RestApiResponse.of(LOGOUT_SUCCESS));
        } catch (Exception e) {
            logger.error("Logout failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestApiResponse.of(LOGOUT_SUCCESS));
        }
    }
}
