package com.kinder.kindergarten.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Log4j2
public class ParentFormDTO {

    // 학부모가 회원가입 할 때 넘어오는 가입 정보

    @NotBlank(message = "성함은 필수 입력 값 입니다.")
    private String parent_name; // 학부모 이름

    @NotBlank(message = "생년월일은 필수 입력 값 입니다.")
    private String parent_birthDate; // 학부모 생년월일

    @NotEmpty(message = "이메일은 필수 입력 값 입니다.")
    @Email(message = "이메일 형식으로 입력해 주시길 바랍니다.")
    private String parent_email;   //학부모 이메일

    @NotEmpty(message = "비밀번호는 필수 입력 값 입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 16자 이하로 입력해 주세요.")
    private String parent_pw; //학부모 비밀번호

    @NotEmpty(message = "비밀번호 재입력은 필수 입력 값 입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 16자 이하로 입력해 주세요.")
    private String parent_pw2;  // 비밀번호 재확인 입력

    @NotEmpty(message = "휴대폰 번호는 필수 입력 값 입니다.")
    private String parent_phone;   //학부모 핸드폰 번호

    @NotEmpty(message = "주소는 필수 입력 값 입니다.")
    private String parent_address; //학부모 주소

}
