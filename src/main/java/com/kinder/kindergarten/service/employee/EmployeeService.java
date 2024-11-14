package com.kinder.kindergarten.service.employee;


import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;

import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.exception.OutOfStockException;
import com.kinder.kindergarten.repository.MemberRepository;
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
public class EmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final FileService fileService;

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
        dto.setProfileImageUrl(employee.getProfileImageUrl());
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

        // 기본 수정 가능 필드 (모든 권한)
        employee.setName(employeeDTO.getName());
        employee.setPhone(employeeDTO.getPhone());
        employee.setAddress(employeeDTO.getAddress());
        employee.setProfileImageUrl(employeeDTO.getProfileImageUrl());

        // 관리자 전용 수정 필드
        if (employeeDTO.getEmail() != null) employee.setEmail(employeeDTO.getEmail());
        if (employeeDTO.getPosition() != null) employee.setPosition(employeeDTO.getPosition());
        if (employeeDTO.getDepartment() != null) employee.setDepartment(employeeDTO.getDepartment());
        if (employeeDTO.getStatus() != null) employee.setStatus(employeeDTO.getStatus());

        employeeRepository.save(employee);
    }

    // 프로필 이미지 업데이트
    @Transactional
    public void updateProfileImage(Long employeeId, MultipartFile file) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        // 기존 프로필 이미지가 있다면 삭제
        if (employee.getProfileImageUrl() != null) {
            fileService.deleteFile(fileService.getFullPath(employee.getProfileImageUrl()));
        }

        // 새 이미지 업로드 및 저장
        String newImageName = fileService.uploadProfileImage(file, employee);
        employee.setProfileImageUrl(newImageName);
        employeeRepository.save(employee);
    }

    // 프로필 이미지 삭제
    @Transactional
    public void deleteProfileImage(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        if (employee.getProfileImageUrl() != null) {
            fileService.deleteFile(fileService.getFullPath(employee.getProfileImageUrl()));
            employee.setProfileImageUrl(null);
            employeeRepository.save(employee);
        }
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
