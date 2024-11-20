package com.kinder.kindergarten.repository.employee;


import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByMember(Member member);
    Optional<Certificate> findByIdAndMember(Long id, Member member);
}
