package com.kinder.kindergarten.service.Employee;

import com.kinder.kindergarten.constant.Employee.Status;
import com.kinder.kindergarten.DTO.Employee.AttendanceDTO;
import com.kinder.kindergarten.entity.Attendance;
import com.kinder.kindergarten.entity.Employee;
import com.kinder.kindergarten.repository.Employee.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    // 출근 처리
    public AttendanceDTO checkIn(Employee employee) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        AttendanceDTO attendanceDTO;

        // 이미 출근했는지 확인
        if (attendanceRepository.existsByEmployeeAndDate(employee, today)) {
            attendanceDTO = new AttendanceDTO(null, today, null, null, null, false, "이미 출근 처리되었습니다.");
            return attendanceDTO; // 실패 메시지 반환
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkIn(now)
                .status(determineStatus(now))
                .build();

        attendanceDTO = convertToDTO(attendance);
        attendanceDTO.setSuccess(true); // 성공으로 설정
        attendanceDTO.setMessage("출근 처리되었습니다."); // 성공 메시지

        attendanceRepository.save(attendance);
        return convertToDTO(attendance);
    }

    // 퇴근 처리
    public AttendanceDTO checkOut(Employee employee) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today)
                .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));

        attendance.setCheckOut(LocalTime.now());
        return convertToDTO(attendance);
    }

    // 출근 상태 결정
    private Status determineStatus(LocalTime checkInTime) {
        return checkInTime.isAfter(LocalTime.of(9, 30)) ? Status.지각 : Status.출근;
    }

    // 월별 근태 기록 조회
    public List<AttendanceDTO> getMonthlyAttendance(Employee employee) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return attendanceRepository.findByEmployeeAndDateBetween(employee, start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .at_id(attendance.getId())
                .at_date(attendance.getDate())
                .at_checkIn(attendance.getCheckIn())
                .at_checkOut(attendance.getCheckOut())
                .at_status(attendance.getStatus().toString())
                .build();
    }
}
