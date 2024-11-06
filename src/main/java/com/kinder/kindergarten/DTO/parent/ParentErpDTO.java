package com.kinder.kindergarten.DTO.parent;

import com.kinder.kindergarten.constant.parent.Children_Role;
import com.kinder.kindergarten.constant.parent.ParentType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ParentErpDTO {

    // ERP 에서 등록하는 학부모 정보

    @NotBlank(message = "성함은 필수 입력 값 입니다.")
    private String parentName; // 학부모 성함

    @NotBlank(message = "이메일은 필수 입력 값 입니다.")
    @Column(unique = true, nullable = false)
    private String  parentEmail;    // 학부모 이메일(학부모 구분을 위해 유니크 설정)

    @NotEmpty(message = "휴대폰 번호는 필수 입력 값 입니다.")
    private String parentPhone;   //학부모 핸드폰 번호

    @NotEmpty(message = "주소는 필수 입력 값 입니다.")
    private String parentAddress; //학부모 주소

    private String emergencyContact;    // 비상 연락처

    @Enumerated(EnumType.STRING)
    private ParentType parentType;  // 자녀와의 관계

    // 생성 날짜
    private LocalDate parentCreateDate = LocalDate.now();  // 학부모 등록일

    // 자녀 정보
    private List<Long> childrenIds;    // 자녀의 ID 목록 (자녀 엔티티와 연결)
}
