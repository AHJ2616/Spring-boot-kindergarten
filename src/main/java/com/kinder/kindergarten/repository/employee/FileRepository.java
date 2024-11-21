package com.kinder.kindergarten.repository.employee;


import com.kinder.kindergarten.entity.employee.Member_File;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<Member_File, Long> {
    // List<Member_File> findByMember(Member member);
}
