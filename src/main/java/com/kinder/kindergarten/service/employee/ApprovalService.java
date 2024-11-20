package com.kinder.kindergarten.service.employee;


import com.kinder.kindergarten.DTO.employee.ApprovalDTO;
import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.constant.employee.ApprovalType;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Approval;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Leave;
import com.kinder.kindergarten.repository.employee.ApprovalRepository;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import com.kinder.kindergarten.repository.employee.LeaveRepository;
import com.kinder.kindergarten.service.NotificationService;
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

    private final NotificationService notificationService;

    // 휴가 결재 요청 생성
    public void createLeaveApproval(Member member, Employee employee, Leave leave) {
        Approval approval = Approval.builder()
                .requester(member)  // Member 객체
                .position(employee)    // Employee 객체 (부서장)
                .type(ApprovalType.LEAVE)
                .title(leave.getTitle())
                .content(leave.getLe_reason())
                .status(ApprovalStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .referenceId(leave.getId())
                .build();

        approvalRepository.save(approval);

        // 결재자에게 알림 전송
        notificationService.sendNotification(
                employee.getMember().getEmail(),
                "새로운 휴가 결재 요청",
                member.getName() + "님이 휴가 결재를 요청했습니다.",
                "APPROVAL",
                "/approval/pending"
        );
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
            Employee employee = member.getEmployees();

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

        // 요청자에게 결과 알림 전송
        String title = status == ApprovalStatus.APPROVED ? "결재 승인" : "결재 반려";
        String content = status == ApprovalStatus.APPROVED ?
                "귀하의 결재가 승인되었습니다." :
                "귀하의 결재가 반려되었습니다. 사유: " + rejectReason;

        notificationService.sendNotification(
                approval.getRequester().getEmail(),
                title,
                content,
                "APPROVAL_RESULT",
                "/approval/list"
        );
    }


    //결재 정보 조회
    public Approval findApprovalById(Long id) {
        return approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결재 정보를 찾을 수 없습니다."));
    }

    // 결재 목록 전체 조회
    public List<ApprovalDTO> getRequestedApprovalsAll() {
        // Approval 리스트를 가져오고, DTO로 변환
        List<Approval> approvals = approvalRepository.findAllApprovals(); // 개선된 메서드 호출
        return approvals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // 결재 대기 목록 조회
    public List<ApprovalDTO> getPendingApprovals(Member member) {
        // Member의 Employee 객체를 가져옵니다.
        Employee employee = member.getEmployees();

        // 단일 Employee와 결재 상태를 사용해 결재 대기 목록을 조회합니다.
        List<Approval> approvals = approvalRepository.findByPositionAndStatus(employee, ApprovalStatus.PENDING);

        // Approval 엔티티를 DTO로 변환하여 반환합니다.
        return approvals.stream()
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
    public void createApproval(Member member, Employee employee, String title,
                               String content, ApprovalType type) {
        Approval approval = Approval.builder()
                .requester(member)  // Member 객체
                .position(employee)    // Employee 객체 (부서장)
                .type(type)
                .status(ApprovalStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .title(title)
                .content(content)
                .build();

        approvalRepository.save(approval);

        // 결재자에게 알림 전송
        notificationService.sendNotification(
                employee.getMember().getEmail(),
                "새로운 결재 요청",
                member.getName() + "님이 결재를 요청했습니다.",
                "APPROVAL",
                "/approval/pending"
        );
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
