package com.kinder.kindergarten.DTO.children;

import com.kinder.kindergarten.entity.children.ClassRoom;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ChildrenErpDTO {

    // ERP 에서 등록하는 유아 정보

    // 유아 이름, 생년월일, 학부모 이름, 알레르기 정보, 병력 정보, 학부모 정보, 성별, 혈액형..


    @NotBlank(message = "원아의 이름은 필수값 입니다.")
    private String childrenName;    // 원아의 이름

    @NotNull(message = "원아의 생년월일은 필수값 입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 날짜 형식 지정
    private LocalDate childrenBirthDate;// 원아의 생년월일

    @NotBlank(message = "원아의 성별은 필수값 입니다.")
    private String childrenGender;  // 원아의 성별

    private String childrenBloodType ;  // 원아의 혈액형

    @NotNull(message = "원아의 반 정보는 필수값 입니다.")
    private String className;   // 원아가 속해있는 반 정보

    private String childrenAllergies; // 원아의 알레르기 정보

    private String childrenMedicalHistory;  // 원아의 병력 정보

    @Lob
    private String	childrenNotes; // 기타 사항

    @NotNull(message = "학부모 정보는 필수입니다.")
    private Long parentId;

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 날짜 형식 지정
    private LocalDate enrollmentDate = LocalDate.now(); // 원아 등록 일자
}
