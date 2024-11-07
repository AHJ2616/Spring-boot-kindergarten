package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.entity.employee.Approval;
import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByEmployeeOrderByRequestDateDesc(Employee employee);
    List<Approval> findByStatusOrderByRequestDateDesc(String status);
}
