package com.kinder.kindergarten.DTO.Employee;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDTO {

    private Long ce_id;            // 자격증 기본키
    private String ce_name;        // 자격증 이름
    private LocalDate ce_issued;   // 자격증 취득 날짜
    private LocalDate ce_expri;    // 만료일
}
