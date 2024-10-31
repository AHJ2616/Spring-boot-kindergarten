package com.kinder.kindergarten.repository.Employee;

import com.kinder.kindergarten.entity.Employee.Education;
import com.kinder.kindergarten.entity.Employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByEmployee(Employee employee);
}
