package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail (String email);

    // 이메일로 중복 체크
    boolean existsByEmail(String email);

    // 전화번호로 중복 체크
    boolean existsByPhone(String phone);

    Optional<Member> findById (Long id);

}
