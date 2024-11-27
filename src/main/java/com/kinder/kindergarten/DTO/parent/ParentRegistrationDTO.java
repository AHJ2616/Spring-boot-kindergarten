package com.kinder.kindergarten.DTO.parent;

import com.kinder.kindergarten.constant.parent.ParentType;
import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.parent.Parent;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ParentRegistrationDTO {

    private Long parentId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String childrenEmergencyPhone;
    private ParentType parentType;
    private RegistrationStatus status;
    private String rejectReason;
    private LocalDateTime registrationDate;
    private LocalDateTime approvalDate;

    @Builder
    public ParentRegistrationDTO(Parent parent, Member member) {
        this.parentId = parent.getParentId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.childrenEmergencyPhone = parent.getChildrenEmergencyPhone();
        this.parentType = parent.getParentType();
        this.status = parent.getRegistrationStatus();
        this.rejectReason = parent.getRejectReason();
        this.registrationDate = parent.getCreatedDate();
        this.approvalDate = parent.getApprovedAt();
    }
}
