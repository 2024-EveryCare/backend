package com.everycare.backend.domain.member.entity;

import com.everycare.backend.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE member_id = ?")
@Where(clause = "is_deleted IS NULL")
@Table(name = "member")
public class Member extends BaseEntity {
    //필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", unique = true, nullable = false)
    private Long id;

    @Column(length = 15, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 20)
    private LocalDate birthdate;


    //빌더
    @Builder
    public Member(String name, String password, String email, Gender gender, LocalDate birthdate) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.birthdate = birthdate;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setbirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
}
