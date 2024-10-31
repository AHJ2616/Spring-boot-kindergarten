package com.kinder.kindergarten.service.Employee;

import com.kinder.kindergarten.constant.Employee.DayOff;
import com.kinder.kindergarten.DTO.Employee.LeaveDTO;
import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.entity.Employee.Leave;
import com.kinder.kindergarten.repository.Employee.EmployeeRepository;
import com.kinder.kindergarten.repository.Employee.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    // 휴가 신청
    public void requestLeave(LeaveDTO leaveDTO, Employee employee) {
        // 연차 잔여일수 확인
        double requestedLeaveDays = calculateLeaveDays(leaveDTO);
        if (employee.getAnnualLeave() < requestedLeaveDays) {
            throw new IllegalStateException("연차 잔여일수가 부족합니다.");
        }

        Leave leave = Leave.builder()
                .employee(employee)
                .start(leaveDTO.getLe_start())
                .end(leaveDTO.getLe_end())
                .type(DayOff.valueOf(leaveDTO.getLe_type()))
                .total(calculateLeaveDays(leaveDTO))
                .status("대기")
                .build();
        employee = Employee.builder()
                .annualLeave(employee.getAnnualLeave() - requestedLeaveDays)
                .build();
        employeeRepository.save(employee);
        leaveRepository.save(leave);
    }

    // 휴가 일수 계산
    private double calculateLeaveDays(LeaveDTO dto) {
        double daysBetween = ChronoUnit.DAYS.between(dto.getLe_start(), dto.getLe_end()) + 1; // 포함하기 위해 +1

        // 요청된 휴가의 타입에 따른 실제 사용일수 반환
        DayOff dayOffType = DayOff.valueOf(dto.getLe_type());
        return daysBetween * dayOffType.getDays();
    }

    // 직원별 휴가 조회
    public List<LeaveDTO> getLeavesByEmployee(Employee employee) {
        return leaveRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LeaveDTO convertToDTO(Leave leave) {
        return LeaveDTO.builder()
                .le_id(leave.getId())
                .le_start(leave.getStart())
                .le_end(leave.getEnd())
                .le_type(leave.getType().toString())
                .le_total(leave.getTotal())
                .le_status(leave.getStatus())
                .build();
    }
}
