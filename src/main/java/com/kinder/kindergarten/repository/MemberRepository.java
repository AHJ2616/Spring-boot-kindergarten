package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,String> {

  Member findByEmail(String email);

}
