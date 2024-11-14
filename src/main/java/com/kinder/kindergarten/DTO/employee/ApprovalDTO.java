package com.kinder.kindergarten.DTO.employee;

import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.constant.employee.ApprovalType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDTO {
    private Long id;
    private String requesterName;
    private String position;
    private ApprovalType type;
    private ApprovalStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime processDate;
    private String rejectReason;
    private String title;
    private String content;
    private Long referenceId;
}
