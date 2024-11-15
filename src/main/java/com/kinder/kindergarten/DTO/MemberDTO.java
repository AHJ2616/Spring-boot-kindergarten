package com.kinder.kindergarten.DTO;

import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class MemberDTO {

    private Long id;                // 기본키
    private String profileImage;    // 프로필 이미지

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;            // 이름

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;           // 이메일

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;        // 비밀번호

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;         // 주소

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호 형식은 000-0000-0000입니다.")
    private String phone;           // 전화번호

    private String role;            // 권한

    private MultipartFile profileImage_upload; // 파일 업로드용
}
