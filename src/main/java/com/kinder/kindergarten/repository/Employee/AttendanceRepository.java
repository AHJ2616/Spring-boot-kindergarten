package com.kinder.kindergarten.repository.Employee;

import com.kinder.kindergarten.entity.Attendance;
import com.kinder.kindergarten.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);
    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate start, LocalDate end);
}
