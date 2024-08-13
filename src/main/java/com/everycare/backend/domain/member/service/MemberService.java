package com.everycare.backend.domain.member.service;

import com.everycare.backend.domain.member.dto.MemberInfoResponse;
import com.everycare.backend.domain.member.dto.SignupRequest;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.entity.Role;
import com.everycare.backend.domain.member.exception.EmailAlreadyExistsException;
import com.everycare.backend.domain.member.exception.InvalidEmailFormatException;
import com.everycare.backend.domain.member.exception.InvalidPasswordFormatException;
import com.everycare.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@%^\\-]).{8,}$";

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return member;
    }

    public boolean checkPassword(Member member, String rawPassword) {
        // 비밀번호 해싱 및 비교 로직을 구현
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }


    public Member getMemberById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get();
        } else {
            throw new RuntimeException("Member not found");
        }
    }

    public Member createMember(SignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (!isValidEmail(signupRequest.getEmail())) {
            throw new InvalidEmailFormatException();
        }
        if (!isValidPassword(signupRequest.getPassword())) {
            throw new InvalidPasswordFormatException();
        }

        Member member = new Member();
        member.setEmail(signupRequest.getEmail());
        member.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        member.setName(signupRequest.getName());
        member.setGender(signupRequest.getGender());
        member.setbirthdate(signupRequest.getBirthdate()); // 날짜 형식 "yyyy-MM-dd"
        member.setRole(Role.USER);


        return memberRepository.save(member);
    }

//    public Member updateMember(Long id, Member updatedMember) {
//        return memberRepository.findById(id).map(existingMember -> {
//            existingMember.setName(updatedMember.getName());
//            existingMember.setPassword(updatedMember.getPassword());
//            existingMember.setEmail(updatedMember.getEmail());
//            existingMember.setContact(updatedMember.getContact());
//            existingMember.setGender(updatedMember.getGender());
//            existingMember.setBirthDate(updatedMember.getBirthDate());
//            return memberRepository.save(existingMember);
//        }).orElseThrow(() -> new RuntimeException("Member not found"));
//    }

    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        member.setDeletedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    public boolean emailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(member.getEmail(), member.getPassword(), new ArrayList<>());
    }



    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = getMemberById(memberId);

        // 나이 계산
        int age = calculateAge(member.getBirthdate());

        // 성별 변환
        String genderStr = "F".equalsIgnoreCase(String.valueOf(member.getGender())) ? "여" : "남";

        MemberInfoResponse response = new MemberInfoResponse();
        response.setName(member.getName());
        response.setBirthdate(member.getBirthdate());
        response.setGenderStr(genderStr);
        response.setAge(age);


        return response;
    }

    private int calculateAge(LocalDate birthdate) {
        if (birthdate == null) {
            throw new IllegalArgumentException("생년월일이 제공되지 않았습니다.");
        }
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }
}
