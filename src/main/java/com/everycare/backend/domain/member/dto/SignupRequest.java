package com.everycare.backend.domain.member.dto;

import com.everycare.backend.domain.member.entity.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private Gender gender;
    private LocalDate BirthDate;

}