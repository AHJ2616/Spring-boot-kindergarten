package com.kinder.kindergarten.service.employee;


import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.DTO.MultiDTO;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import com.kinder.kindergarten.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final FileService fileService;

    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    // 4. 정보 업데이트
    @Transactional
    public void updateEmployee(MultiDTO multiDTO) {

        EmployeeDTO employeeDTO = multiDTO.getEmployeeDTO();
        MemberDTO memberDTO = multiDTO.getMemberDTO();

        Employee employee = employeeRepository.findById(multiDTO.getEmployeeDTO().getId())
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(multiDTO.getMemberDTO().getEmail());

        // 관리자 전용 수정 필드
        if (employeeDTO.getPosition() != null) employee.setPosition(employeeDTO.getPosition());
        if (employeeDTO.getDepartment() != null) employee.setDepartment(employeeDTO.getDepartment());
        if (employeeDTO.getStatus() != null) employee.setStatus(employeeDTO.getStatus());
        if (employeeDTO.getSalary() != null) employee.setSalary(employeeDTO.getSalary());
        if (employeeDTO.getHireDate() != null) employee.setHireDate(employeeDTO.getHireDate());

        // MemberDTO에서 이름, 주소, 전화번호 등의 필드를 수정
        if (memberDTO.getName() != null) member.setName(memberDTO.getName());
        if (memberDTO.getPassword() != null) member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        if (memberDTO.getAddress() != null) member.setAddress(memberDTO.getAddress());
        if (memberDTO.getPhone() != null) member.setPhone(memberDTO.getPhone());
        if (memberDTO.getEmail() != null) member.setEmail(memberDTO.getEmail());


        employeeRepository.save(employee);
        memberRepository.save(member);
    }

    // 4-1. 프로필 이미지 업데이트
    @Transactional
    public void updateProfileImage(Long employeeId, MultipartFile file) {
        Member member = memberRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        // 새 이미지 업로드 및 저장
        String newImageName = fileService.uploadProfileImage(file, member);
        member.setProfileImage(newImageName);
        memberRepository.save(member);
    }

    // 결재 요청자
    @Transactional(readOnly = true)
    public Employee getEmployeeEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));
    }

    // 결재자
    public Employee findDepartmentHead(String department) {
        return employeeRepository.findByDepartmentAndPositionLevelGreaterThanEqual(department, 4)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("부서장을 찾을 수 없습니다."));
    }
    // 결재자
    public List<Employee> findApprovers() {
        return employeeRepository.findByPositionLevelGreaterThanEqual(3); // 예: 과장급 이상
    }

}
