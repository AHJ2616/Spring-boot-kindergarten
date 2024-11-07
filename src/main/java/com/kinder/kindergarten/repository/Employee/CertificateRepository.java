package com.kinder.kindergarten.repository.employee;


import com.kinder.kindergarten.entity.employee.Certificate;
import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByEmployee(Employee employee);
}
