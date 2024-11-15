package com.kinder.kindergarten.entity.employee;

import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.constant.employee.ApprovalType;
import com.kinder.kindergarten.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private Member requester; // 결재 요청자

    @ManyToOne
    @JoinColumn(name = "employee_position")
    private Employee position; // 결재자

    @Column(name = "approval_type")
    @Enumerated(EnumType.STRING)
    private ApprovalType type; // 결재 종류 (휴가, 지출 등)

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status; // 결재 상태

    @Column(name = "request_date")
    private LocalDateTime requestDate; // 요청일시

    @Column(name = "process_date")
    private LocalDateTime processDate; // 처리일시

    @Column(name = "reject_reason")
    private String rejectReason; // 반려 사유

    @Column(name = "reference_id")
    private Long referenceId; // 참조 문서 ID (휴가 신청 ID 등)

    @Column(name = "approval_title")
    private String title;

    @Column(name = "approval_content")
    private String content;
}
