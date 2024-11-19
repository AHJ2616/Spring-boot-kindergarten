package com.kinder.kindergarten.entity.employee;

import com.kinder.kindergarten.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "Certificate")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {

    @Id
    @Column(name = "certificate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자격증 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 교사 기본키

    @Column(name = "certificate_name")
    private String name; // 자격증 이름

    @Column(name = "certificate_issued")
    private LocalDate issued; // 자격증 발급날짜

    @Column(name = "certificate_expri")
    private LocalDate expri; // 자격증 만료일

    @Column(name = "certificate_path")
    private String path; // 자격증 파일 여부



}
