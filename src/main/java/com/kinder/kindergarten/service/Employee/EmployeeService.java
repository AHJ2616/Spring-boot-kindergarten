package com.kinder.kindergarten.service.employee;


import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.exception.OutOfStockException;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee){
        // 직원등록 메서드
        validateDuplicateEmployee(employee);
        return employeeRepository.save(employee);
    }

    private void validateDuplicateEmployee(Employee employee){
        // 직원등록 메서드
        Employee em = employeeRepository.findByEmail(employee.getEmail());
        if(em != null){
            throw new OutOfStockException("이미 등록되어있는 직원입니다.");
        }
    }

    @Override
    // 로그인 메서드
    public UserDetails loadUserByUsername(String email){
        // 이메일 정보를 받아 처리
        Employee employee = employeeRepository.findByEmail(email);
        return new PrincipalDetails(employee);
    }

    public EmployeeDTO readEmployee(Long id){
        // id값으로 직원찾기
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("직원을 찾을 수 없습니다."));
        return covertToDTO(employee);
    }

    private EmployeeDTO covertToDTO(Employee employee){
        // 엔티티로 넘어온 값을 DTO 타입으로 변형
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setCleanup(employee.getCleanup());
        dto.setEmail(employee.getEmail());
        dto.setAddress(employee.getAddress());
        dto.setPassword(employee.getPassword());
        dto.setPhone(employee.getPhone());
        dto.setPosition(String.valueOf(employee.getPosition()));
        dto.setDepartment(employee.getDepartment());
        dto.setAnnualLeave(employee.getAnnualLeave());
        dto.setStatus(employee.getStatus());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        return dto;
    }

    public void deleteEmployee(Long id){
        employeeRepository.deleteById(id);
    }

    // 이메일 중복 여부 체크
    public boolean isEmailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    // 전화번호 중복 여부 체크
    public boolean isPhoneExists(String phone) {
        return employeeRepository.existsByPhone(phone);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::covertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));
        return covertToDTO(employee);
    }

    @Transactional
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(employeeDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        employee.setName(employeeDTO.getName());
        employee.setPhone(employeeDTO.getPhone());
        employee.setPosition(employeeDTO.getPosition());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setStatus(employeeDTO.getStatus());
        employee.setSalary(employeeDTO.getSalary());

        employeeRepository.save(employee);
    }

}
