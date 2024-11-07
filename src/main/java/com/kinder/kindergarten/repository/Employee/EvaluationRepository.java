package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByEmployee(Employee employee);
    List<Evaluation> findByEmployeeOrderByEvaluationDateDesc(Employee employee);
}
