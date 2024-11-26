package com.kinder.kindergarten.DTO.parent;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailFindDTO {

  /* 학부모 회원이 로그인 할 때 본인의 이메일 계정을 잊어버릴 경우 이메일 찾기 정보.

    11.18 -> 아직 로그인에 계정 찾기(이메일) 기능은 없으나 추후 추가 하실 때를 위해 남겨두었습니다.
   */

  @NotEmpty(message = "성함을 입력해 주세요.")
  private String parent_name;

  @NotEmpty(message = "법정생년월일 6자리를 입력해 주세요.")
  private String parent_birthDate;

  @NotEmpty(message = "휴대폰 번호를 입력해 주세요.")
  private String parent_phone;
}