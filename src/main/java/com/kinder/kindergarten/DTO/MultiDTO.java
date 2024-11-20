package com.kinder.kindergarten.DTO;

import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultiDTO {

    // 한 form 에서 2가지 DTO 사용 가능
    private MemberDTO memberDTO;
    private EmployeeDTO employeeDTO;
}