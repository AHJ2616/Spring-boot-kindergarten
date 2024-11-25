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

    Page<Member> findByNameContaining(String keyword, Pageable pageable);
    // 학부모 성함으로 검색하는 메서드

    boolean existsByEmail(String email);
    // 이메일 존재 여부 확인하는 메서드

    List<Member> findByNameContainingAndRole(String name, Role role);
    // 학부모 이름으로 검색하는 메서드

    List<Member> findByPhoneContainingAndRole(String phone, Role role);
    // 학부모 핸드폰 번호로 검색하는 메서드
}
