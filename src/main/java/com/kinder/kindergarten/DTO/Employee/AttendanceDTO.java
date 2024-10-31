package com.kinder.kindergarten.DTO.Employee;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDTO {

    private Long at_id;            // 근태관리 기본키
    private LocalDate at_date;     // 출근날짜
    private LocalTime at_checkIn;  // 출근시간
    private LocalTime at_checkOut; // 퇴근시간
    private String at_status;      // 상태 (출근, 지각, 결근)
    private boolean success;        // 처리 성공 여부
    private String message;         // 처리 결과 메시지
}
