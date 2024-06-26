package com.everycare.backend.domain.member.service;

import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

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
}
