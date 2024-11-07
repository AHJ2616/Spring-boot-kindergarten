package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);

    // 이메일로 중복 체크
    boolean existsByEmail(String email);

    // 전화번호로 중복 체크
    boolean existsByPhone(String phone);
}
