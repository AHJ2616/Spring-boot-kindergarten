package com.kinder.kindergarten.repository.employee;


import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByMemberAndDate(Member member, LocalDate date);
    Optional<Attendance> findByMemberAndDate(Member member, LocalDate date);
    List<Attendance> findByMemberAndDateBetween(Member member, LocalDate start, LocalDate end);
}
