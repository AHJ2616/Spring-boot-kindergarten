package com.kinder.kindergarten.DTO.parent;


import com.kinder.kindergarten.constant.parent.ParentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ParentErpDTO {

    // ERP 에서 등록하는 학부모 정보

    private Long parentId; //학부모 고유 ID (PK)

    @NotBlank(message = "성함은 필수 입력 값 입니다.")
    private String parentName; // 학부모 성함

    @NotBlank(message = "이메일은 필수 입력 값 입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String parentEmail;    // 학부모 이메일(학부모 구분을 위해 유니크 설정)

    private String parentPassword; //학부모 비밀번호(자동 생성용)

    @NotBlank(message = "연락처는 필수 입력값입니다.")
    private String parentPhone;   //학부모 핸드폰 번호

    @NotBlank(message = "주소는 필수 입력 값 입니다.")
    private String parentAddress; //학부모 주소

    private String childrenEmergencyPhone;    // 비상 연락처

    @Enumerated(EnumType.STRING)
    private ParentType parentType;  // 자녀와의 관계

    // 생성 날짜
    private LocalDate parentCreateDate = LocalDate.now();  // 학부모 등록일

    // 자녀 정보
    private List<Long> childrenIds;    // 자녀의 ID 목록 (자녀 엔티티와 연결)

    private String tempPassword;// 임시 비밀번호(화면 표시용)
}
