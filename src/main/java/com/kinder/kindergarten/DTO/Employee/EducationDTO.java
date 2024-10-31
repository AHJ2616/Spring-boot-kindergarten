package com.kinder.kindergarten.DTO.Employee;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {

    private Long ed_id;             // 교육 기본키
    private String ed_name;         // 교육이름
    private LocalDate ed_start;     // 교육시작 날짜
    private LocalDate ed_end;       // 교육 종료 날짜
    private String ed_certificate;  // 수료증 유무
}
