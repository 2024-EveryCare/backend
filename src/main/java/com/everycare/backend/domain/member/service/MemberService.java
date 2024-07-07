package com.everycare.backend.domain.member.service;

import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public boolean checkPassword(Optional<Member> memberOptional, String password) {
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            // 비밀번호 해싱 및 비교 로직을 구현
            return member.getPassword().equals(password);
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

    public Member createMember(Member member) {
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
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
}
