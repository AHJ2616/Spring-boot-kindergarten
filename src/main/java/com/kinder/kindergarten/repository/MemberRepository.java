package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByEmail (String email);

    boolean existsByEmail(String email);
    // 이메일 존재 여부 확인하는 메서드

    // 이름으로 검색 (페이징)
    Page<Member> findByNameContainingAndRole(String name, Role role, Pageable pageable);

    Page<Member> findByRole(Role role, Pageable pageable);
}
