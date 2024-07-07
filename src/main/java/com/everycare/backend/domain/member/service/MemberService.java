package com.everycare.backend.domain.member.service;

import com.everycare.backend.domain.member.dto.SignupRequest;
import com.everycare.backend.domain.member.entity.Member;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public boolean checkPassword(Optional<Member> memberOptional, String rawPassword) {
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            // 비밀번호 해싱 및 비교 로직을 구현
            return passwordEncoder.matches(rawPassword, member.getPassword());
        } else {
            return false;
        }
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
        if (memberRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
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
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
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
}
