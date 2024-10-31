package com.kinder.kindergarten.repository.Employee;

import com.kinder.kindergarten.entity.Employee.Approval;
import com.kinder.kindergarten.entity.Employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByEmployeeOrderByRequestDateDesc(Employee employee);
    List<Approval> findByStatusOrderByRequestDateDesc(String status);
}
