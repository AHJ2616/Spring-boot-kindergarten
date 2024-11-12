package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity,String> {

  MemberEntity findbymember_email(String email);

}
