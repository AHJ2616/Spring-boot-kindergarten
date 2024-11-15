package com.kinder.kindergarten.service.employee;


import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;

import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.exception.OutOfStockException;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import com.kinder.kindergarten.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService{

    private final EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee){
        // 직원등록 메서드
        return employeeRepository.save(employee);
    }


    private EmployeeDTO covertToDTO(Employee employee){
        // 엔티티로 넘어온 값을 DTO 타입으로 변형
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setCleanup(employee.getCleanup());
        dto.setPosition(String.valueOf(employee.getPosition()));
        dto.setDepartment(employee.getDepartment());
        dto.setAnnualLeave(employee.getAnnualLeave());
        dto.setStatus(employee.getStatus());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        return dto;
    }

    @Transactional
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(employeeDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        // 관리자 전용 수정 필드
        if (employeeDTO.getPosition() != null) employee.setPosition(employeeDTO.getPosition());
        if (employeeDTO.getDepartment() != null) employee.setDepartment(employeeDTO.getDepartment());
        if (employeeDTO.getStatus() != null) employee.setStatus(employeeDTO.getStatus());

        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));
    }

    public Employee findDepartmentHead(String department) {
        return employeeRepository.findByDepartmentAndPositionLevelGreaterThanEqual(department, 4)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("부서장을 찾을 수 없습니다."));
    }

    public List<Employee> findApprovers() {
        return employeeRepository.findByPositionLevelGreaterThanEqual(3); // 예: 과장급 이상
    }

}
