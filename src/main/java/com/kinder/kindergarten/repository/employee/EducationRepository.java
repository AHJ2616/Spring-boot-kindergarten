package com.kinder.kindergarten.repository.employee;


import com.kinder.kindergarten.entity.employee.Education;
import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByEmployee(Employee employee);
    Optional<Education> findByIdAndEmployee(Long id, Employee employee);
}
