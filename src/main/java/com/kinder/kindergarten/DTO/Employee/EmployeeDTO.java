package com.kinder.kindergarten.DTO.Employee;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

@Getter @Setter
public class EmployeeDTO {

    // 기본키
    private Long id;
    // 사번
    private String cleanup;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "\\d{3}-\\d{3,4}-\\d{4}", message = "전화번호 형식은 000-0000-0000입니다.")
    private String phone;

    @NotBlank(message = "직위는 필수 입력 값입니다.")
    private String position;

    @NotBlank(message = "부서는 필수 입력 값입니다.")
    private String department;

    @Pattern(regexp = "재직|퇴사", message = "유효하지 않은 재직상태입니다.")
    private String status;

    @DecimalMin(value = "0", message = "급여는 0 이상이어야 합니다.")
    private BigDecimal salary;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    // 잔여 연차
    private int annualLeave;

}
