package com.everycare.backend.domain.member.controller;

import com.everycare.backend.domain.member.dto.LoginRequest;
import com.everycare.backend.domain.member.dto.LoginResponse;
import com.everycare.backend.domain.member.dto.SignupRequest;
import com.everycare.backend.domain.member.dto.SignupResponse;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> registerUser(@RequestBody SignupRequest signupRequest) {
        if (memberService.emailExists(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SignupResponse("이메일이 이미 존재합니다", false));
        }

        Member member = new Member();
        member.setEmail(signupRequest.getEmail());
        member.setPassword(signupRequest.getPassword()); // 암호화된 비밀번호 저장을 권장
        member.setName(signupRequest.getName());
        member.setGender(signupRequest.getGender());
        member.setbirthdate(signupRequest.getBirthDate()); // 날짜 형식은 "yyyy-MM-dd"로 가정

        memberService.createMember(member);

        return ResponseEntity.ok(new SignupResponse("회원 가입 성공", true));
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
