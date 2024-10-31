package com.kinder.kindergarten.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Log4j2
public class ChildrenFormDTO {

    // 학부모가 원아를 등록할때 등록 화면으로부터 넘어오는 등록 정보

    private String	children_name;  //원아 이름

    private LocalDate birthDate;  //생년월일

    private String	parent_name;    //학부모 이름

    private String	parent_phone;   //학부모 연락처

    private String	children_emergencyPhone;    //긴급 연락처

    private String	children_allergies; //알레르기 정보

    private String	children_medicalHistory;    //병력 정보

    private String	children_notes; //기타 사항

}
