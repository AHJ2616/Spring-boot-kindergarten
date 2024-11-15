package com.kinder.kindergarten.service.employee;


import com.kinder.kindergarten.DTO.employee.ApprovalDTO;
import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.constant.employee.ApprovalType;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Approval;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Leave;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.employee.ApprovalRepository;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import com.kinder.kindergarten.repository.employee.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    private final MemberRepository memberRepository;

    // 휴가 결재 요청 생성
    public void createLeaveApproval(Member requester, Employee approver, Leave leave) {
        Approval approval = Approval.builder()
                .requester(requester)  // Member 객체
                .position(approver)    // Employee 객체 (부서장)
                .type(ApprovalType.LEAVE)
                .title(leave.getTitle())
                .content(leave.getLe_reason())
                .status(ApprovalStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .referenceId(leave.getId())
                .build();

        approvalRepository.save(approval);
    }

    // 결재 처리
    @Transactional
    public void processApproval(Long approvalId, ApprovalStatus status, String rejectReason) {
        // 결재 정보 조회
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 정보를 찾을 수 없습니다."));

        approval.setStatus(status);
        approval.setProcessDate(LocalDateTime.now());
        approval.setRejectReason(rejectReason);

        // 휴가 결재인 경우
        if (approval.getType() == ApprovalType.LEAVE) {

            // 해당 휴가 신청 조회
            Leave leave = leaveRepository.findById(approval.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("휴가 신청을 찾을 수 없습니다."));

            // Leave와 연결된 Member에서 Employee 정보 가져오기
            Member member = leave.getMember();  // Leave는 Member를 참조함
            Employee employee = member.getEmployees()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다."));

            // 결재 승인 처리
            if (status == ApprovalStatus.APPROVED) {
                leave.setStatus("승인");
                employee.setAnnualLeave(employee.getAnnualLeave() - leave.getTotal());  // 연차 잔여일수 차감
            } else if (status == ApprovalStatus.REJECTED) {
                leave.setStatus("반려");
                leave.setRejectReason(rejectReason);
            }

            // Employee와 Leave 정보 저장
            employeeRepository.save(employee);
            leaveRepository.save(leave);
        }

        // 결재 처리 완료 후 결재 정보 저장
        approvalRepository.save(approval);
    }

    //결재 정보 조회
    public Approval findApprovalById(Long id) {
        return approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결재 정보를 찾을 수 없습니다."));
    }

    // 결재 대기 목록 조회
    public List<ApprovalDTO> getPendingApprovals(Member member) {
        // Member의 Employee 리스트를 가져옵니다.
        List<Employee> employees = member.getEmployees();

        // Employee 리스트를 사용해 결재 대기 목록을 조회합니다.
        return approvalRepository.findByPositionInAndStatus(employees, ApprovalStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 결재 요청 목록 조회
    public List<ApprovalDTO> getRequestedApprovals(Member member) {
        return approvalRepository.findByRequester(member)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 결재 요청 생성
    public void createApproval(Member requester, Employee position, String title,
                               String content, ApprovalType type) {
        Approval approval = Approval.builder()
                .requester(requester)
                .position(position)
                .type(type)
                .status(ApprovalStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .title(title)
                .content(content)
                .build();

        approvalRepository.save(approval);
    }

    private ApprovalDTO convertToDTO(Approval approval) {
        return ApprovalDTO.builder()
                .id(approval.getId())
                .requesterName(approval.getRequester().getName())
                .position(approval.getPosition().getPosition())
                .type(approval.getType())
                .status(approval.getStatus())
                .requestDate(approval.getRequestDate())
                .processDate(approval.getProcessDate())
                .rejectReason(approval.getRejectReason())
                .title(approval.getTitle())
                .content(approval.getContent())
                .referenceId(approval.getReferenceId())
                .build();
    }
}
