package com.kinder.kindergarten.DTO;


import com.kinder.kindergarten.entity.Parent;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@NoArgsConstructor
public class ParentUpdateFormDTO {

    // 학부모가 회원 정보를 수정할 때 받는 DTO
    // 회원 정보 수정할 때 수정 사항 : 학부모 성함, 휴대폰 번호, 주소
    // 비밀번호는 따로 비밀번호 찾기 서비스를 만들 예정


    @NotEmpty(message = "성함은 필수 입력 값 입니다.")
    private String parent_name; // 수정할 학부모 성함(ex: 개명 등)

    private String parent_email;    // 이메일 정보는 나타날 뿐 수정은 못하게 처리(Front -> ReadOnly 처리)

    private String parent_birthDate;    // 생년월일 정보도 이메일과 같이 수정은 못하게 처리(Front -> ReadOnly 처리)
    @NotEmpty(message = "핸드폰 번호는 필수 입력 값 입니다.")
    private String parent_phone;// 수정할 학부모 휴대폰 번호(ex: 번호이동, 기종변경)

    private String parent_address;// 수정할 학부모의 집 주소(ex: 이사 등)

    public ParentUpdateFormDTO(Parent parent) {
        // Parent 엔티티로 부터 업데이트 DTO를 생성하는 생성자

        this.parent_name = parent.getParent_name();
        this.parent_email = parent.getParent_email();
        this.parent_phone = parent.getParent_phone();
        this.parent_address = parent.getParent_address();
        this.parent_birthDate = parent.getParent_birthDate();
    }



}
