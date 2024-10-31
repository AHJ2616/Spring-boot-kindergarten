package com.kinder.kindergarten.repository.Employee;

import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.entity.Employee.Employee_File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<Employee_File, Long> {
    List<Employee_File> findByEmployee(Employee employee);
}
