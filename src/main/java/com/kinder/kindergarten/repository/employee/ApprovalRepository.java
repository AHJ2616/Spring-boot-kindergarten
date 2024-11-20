package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Approval;
import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByPositionAndStatus(Employee position, ApprovalStatus status); // 결재자와 결재 상태를 기준으로 조회
    List<Approval> findByRequesterAndStatus(Member requester, ApprovalStatus status); // 요청자와 결재 상태를 기준으로 조회
    List<Approval> findByRequester(Member member);
    @Query("SELECT a FROM Approval a JOIN FETCH a.requester JOIN FETCH a.position")
    List<Approval> findAllApprovals();
}
