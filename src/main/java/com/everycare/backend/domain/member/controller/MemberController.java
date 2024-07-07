package com.everycare.backend.domain.member.controller;

import com.everycare.backend.domain.member.dto.*;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@Tag(name = "Example", description = "Example API")
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", produces = "application/json")
    @Operation(summary = "회원가입 API", description = "ID:이메일 형식, PW, 이름, 성별, 생년월일을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = {@Content(schema = @Schema(implementation = SignupResponse.class))}),
            @ApiResponse(responseCode = "400", description = "이메일 중복 또는 기타 오류",
                    content = {@Content(schema = @Schema(implementation = BasicErrorResponse.class))}),
    })
    public ResponseEntity<SignupResponse> registerUser(@RequestBody SignupRequest signupRequest) {
        if (memberService.emailExists(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SignupResponse("M001_2","400", "이메일이 이미 존재합니다"));
        }

        Member member = new Member();
        member.setEmail(signupRequest.getEmail());
        member.setPassword(passwordEncoder.encode(signupRequest.getPassword()));// 암호화된 비밀번호 저장을 권장
        member.setName(signupRequest.getName());
        member.setGender(signupRequest.getGender());
        member.setbirthdate(signupRequest.getBirthdate()); // 날짜 형식은 "yyyy-MM-dd"로 가정

        memberService.createMember(member);

        return ResponseEntity.ok(new SignupResponse("M001","201", "회원 가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<Member> member = memberService.findByEmail(loginRequest.getEmail());

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("이메일 아이디가 틀렸습니다", false));
        }

        if (memberService.checkPassword(member, loginRequest.getPassword())) {
            return ResponseEntity.ok(new LoginResponse("로그인 성공", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("비밀번호가 틀렸습니다", false));
        }
    }
}
