package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployee(Employee employee); // 특정 직원의 휴가 목록 조회
    List<Leave> findByStatus(String status); // 특정 상태의 휴가 목록 조회(승인, 반려, 대기 등)
    List<Leave> findByType(String type); // 특정 유형의 휴가 목록 조회(연차, 반차 등)
}
