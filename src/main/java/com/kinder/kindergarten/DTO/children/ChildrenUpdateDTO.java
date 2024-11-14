package com.kinder.kindergarten.DTO.children;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ChildrenUpdateDTO {

    // ERP에 등록된 원아의 정보를 수정하는 DTO

    private Long childrenId ; // 원아의 고유 ID

    private String childrenName; //원아 이름

    private LocalDate childrenBirthDate ; // 원아 생년월일

    private String childrenGender;  // 원아의 성별

    private String childrenBloodType ;  // 원아의 혈액형

    private String	childrenAllergies;  //알레르기 정보

    private String	childrenMedicalHistory; //병력 정보

    private String	childrenNotes; // 기타 사항
}
