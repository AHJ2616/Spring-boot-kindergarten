package com.kinder.kindergarten.DTO.parent;

import com.kinder.kindergarten.constant.parent.ParentType;
import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCompletionDTO {

    // erp에서 직원이 모든 등록과정을 끝나고 완료 페이지를 보여주기 위한 DTO

    // Member 정보
    private String memberEmail;
    private String memberName;
    private String memberPhone;
    private String memberAddress;

    // Parent 정보
    private Long parentId;
    private String childrenEmergencyPhone;
    private String detailAddress;
    private ParentType parentType;

    // Children 정보
    private Long childrenId;
    private String childrenName;
    private String childrenGender;
    private LocalDate childrenBirthDate;
    private String childrenAllergies;
    private String childrenMedicalHistory;

    // ClassRoom 정보
    private Long classRoomId;
    private String classRoomName;
    private String employeeName;
    private Integer currentStudents;
    private Integer maxChildren;

    // 등록 정보
    private LocalDateTime registrationDate;
}
