package com.kinder.kindergarten.repository.employee;


import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByMemberId(Long memberId);

    // 결재자 찾기 - 요청자보다 높은 직급 중 가장 낮은 직급의 직원을 찾음
    List<Employee> findByPositionLevelGreaterThanEqual(Integer positionLevel);

    List<Employee> findByDepartmentAndPositionLevelGreaterThanEqual(String department, Integer positionLevel);
}
