package com.kinder.kindergarten.DTO.employee;

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
    private String le_type;         // 휴가 유형(연차, 반차)
    private String le_status;       // 승인상태
    private double le_total;        // 사용 연차일수
    private String le_reason;       // 휴가 사유
    private String rejectReason;    // 반려 사유
}
