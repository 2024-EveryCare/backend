//package com.everycare.backend.domain.member.controller;
//
//import com.everycare.backend.domain.member.dto.LoginRequest;
//import com.everycare.backend.domain.member.dto.SignupRequest;
//import com.everycare.backend.domain.member.entity.Member;
//import com.everycare.backend.domain.member.service.MemberService;
////import com.everycare.backend.global.security.JwtTokenProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
////    @Autowired
////    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private MemberService memberService;
//
////    @Autowired
////    private JwtTokenProvider jwtTokenProvider;
//
////    @Autowired
////    private PasswordEncoder passwordEncoder;
//
//    @PostMapping("/api/v1/members/signup")
//    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
//        if (memberService.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity.badRequest().body("Error: Email is already in use!");
//        }
//        Member member = Member.builder()
//                .name(signUpRequest.getName())
//                .email(signUpRequest.getEmail())
//                .password(signUpRequest.getPassword())
//                .contact(signUpRequest.getContact())
//                .gender(signUpRequest.getGender())
//                .birthDate(signUpRequest.getBirthDate())
//                .build();
//        memberService.createMember(member);
//        return ResponseEntity.ok("User registered successfully!");
//    }
//
//    @PostMapping("/api/v1/members/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
////        Authentication authentication = authenticationManager.authenticate(
////                new UsernamePasswordAuthenticationToken(
////                        loginRequest.getEmail(),
////                        loginRequest.getPassword()
////                )
////        );
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////        String jwt = jwtTokenProvider.createToken(loginRequest.getEmail());
////        return ResponseEntity.ok(jwt);
////    }
////}
//    }
