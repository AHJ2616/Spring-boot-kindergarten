package com.kinder.kindergarten.DTO.Employee;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDTO {

    private Long le_id;             // 휴가 기본키
    private LocalDate le_start;     // 휴가 시작일
    private LocalDate le_end;       // 휴가 종료일
    private String le_type;         // 휴가 유형
    private String le_status;       // 상태(연차, 반차, 병가)
    private double le_total;       // 사용 연차일수
}
