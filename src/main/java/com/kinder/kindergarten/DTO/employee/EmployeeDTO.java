package com.kinder.kindergarten.DTO.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class EmployeeDTO {

    // 기본키
    private Long id;
    // 사번
    private String cleanup;

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
    private double annualLeave;

}
