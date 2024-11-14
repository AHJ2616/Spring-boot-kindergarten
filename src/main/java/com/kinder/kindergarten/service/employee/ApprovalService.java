package com.kinder.kindergarten.service.employee;


import com.kinder.kindergarten.DTO.employee.ApprovalDTO;
import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.constant.employee.ApprovalType;
import com.kinder.kindergarten.entity.employee.Approval;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Leave;
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

    // 휴가 결재 요청 생성
    public void createLeaveApproval(Employee requester, Employee approver, Leave leave) {
        Approval approval = Approval.builder()
                .requester(requester)
                .position(approver)
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
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 정보를 찾을 수 없습니다."));

        approval.setStatus(status);
        approval.setProcessDate(LocalDateTime.now());
        approval.setRejectReason(rejectReason);

        // 휴가 결재인 경우
        if (approval.getType() == ApprovalType.LEAVE) {

            Leave leave = leaveRepository.findById(approval.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("휴가 신청을 찾을 수 없습니다."));

            Employee employee = employeeRepository.findByEmail(leave.getEmployee().getEmail());

            if (status == ApprovalStatus.APPROVED) {
                leave.setStatus("승인");
                employee.setAnnualLeave(employee.getAnnualLeave()-leave.getTotal());
            } else if (status == ApprovalStatus.REJECTED) {
                leave.setStatus("반려");
                leave.setRejectReason(rejectReason);
            }
            employeeRepository.save(employee);
            leaveRepository.save(leave);
        }

        approvalRepository.save(approval);
    }

    //결재 정보 조회
    public Approval findApprovalById(Long id) {
        return approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결재 정보를 찾을 수 없습니다."));
    }

    // 결재 대기 목록 조회
    public List<ApprovalDTO> getPendingApprovals(Employee position) {
        return approvalRepository.findByPositionAndStatus(position, ApprovalStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 결재 요청 목록 조회
    public List<ApprovalDTO> getRequestedApprovals(Employee requester) {
        return approvalRepository.findByRequester(requester)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 결재 요청 생성
    public void createApproval(Employee requester, Employee position, String title,
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
                .position(approval.getPosition().getName())
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
