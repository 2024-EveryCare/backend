package com.everycare.backend.domain.member.service;

import com.everycare.backend.domain.member.dto.CustomUserDetails;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//스프링 시큐리티가 인증을 위해 사용하는 서비스
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member userData = memberRepository.findByEmail(email);

        if (userData != null) {

            return new CustomUserDetails(userData);
        }

        return null;
    }
}
