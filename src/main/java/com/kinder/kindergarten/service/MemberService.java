package com.kinder.kindergarten.service;

import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.DTO.MultiDTO;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.entity.FcmTokenEntity;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.exception.OutOfStockException;
import com.kinder.kindergarten.repository.FcmTokenRepository;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final FcmTokenRepository fcmTokenRepository;
    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    // 0. 로그인
    public UserDetails loadUserByUsername(String email){
        // 이메일 정보를 받아 처리
        Member member = memberRepository.findByEmail(email);
        Employee employee = employeeRepository.findByMemberId(member.getId());
        System.out.println(employee);
        return new PrincipalDetails(member, employee);
    }

    // 1. 회원 등록
    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

        // 1. 회원 등록
        public Member saveMember2(MemberDTO memberDTO) {
            Member member = modelMapper.map(memberDTO, Member.class);

    // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);

    // 회원 저장
        return memberRepository.save(member);
    }

    // 1-1. 등록 회원 확인
    private void validateDuplicateMember(Member member){
        Member em = memberRepository.findByEmail(member.getEmail());
        if(em != null){
            throw new OutOfStockException("이미 등록되어있는 회원입니다.");
        }
    }

    // 2. 회원 찾기
    public MultiDTO readMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("직원을 찾을 수 없습니다."));;

        MultiDTO multiDTO = new MultiDTO();
        multiDTO.setMemberDTO(covertToMemberDTO(member));
        multiDTO.setEmployeeDTO(covertToEmployeeDTO((Employee) member.getEmployees()));

        return multiDTO;
    }

    // 2-1. 직원 리스트
    @Transactional(readOnly = true)
    public List<MultiDTO> getAllEmployeeMember() {
        return memberRepository.findAll().stream()
                .filter(member -> member.getRole() != Role.ROLE_PARENT)  // ROLE_PARENT 제외
                .map(member -> {
                    MultiDTO multiDTO = new MultiDTO();
                    multiDTO.setMemberDTO(covertToMemberDTO(member));  // MemberDTO 변환
                    multiDTO.setEmployeeDTO(covertToEmployeeDTO((Employee) member.getEmployees()));  // EmployeeDTO 변환
                    return multiDTO;
                })
                .collect(Collectors.toList());
    }

    // 3. 프로필 이미지 업데이트
    @Transactional
    public void updateProfileImage(Long id, MultipartFile file) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        // 기존 프로필 이미지가 있다면 삭제
        if (member.getProfileImage() != null) {
            fileService.deleteFile(fileService.getFullPath(member.getProfileImage()));
        }

        // 새 이미지 업로드 및 저장
        String newImageName = fileService.uploadProfileImage(file, member);
        member.setProfileImage(newImageName);
        memberRepository.save(member);
    }

    // 3-2. 프로필 이미지 삭제
    @Transactional
    public void deleteProfileImage(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        if (member.getProfileImage() != null) {
            fileService.deleteFile(fileService.getFullPath(member.getProfileImage()));
            member.setProfileImage(null);
            memberRepository.save(member);
        }
    }

    // 3-3. 정보 수정
    @Transactional
    public void updateMember(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        // 기본 수정 가능 필드 (모든 권한)
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
        member.setAddress(memberDTO.getAddress());
        member.setProfileImage(memberDTO.getProfileImage());

        memberRepository.save(member);
    }


    // 4. 엔티티값 DTO로 변환
    private MemberDTO covertToMemberDTO(Member member){
        // 엔티티로 넘어온 값을 DTO 타입으로 변형
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setEmail(member.getEmail());
        dto.setName(member.getName());
        dto.setPhone(member.getPhone());
        dto.setAddress(member.getAddress());
        dto.setProfileImage(member.getProfileImage());
        return dto;
    }

    private EmployeeDTO covertToEmployeeDTO(Employee employee){
        // 엔티티로 넘어온 값을 DTO 타입으로 변형
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setDepartment(employee.getDepartment());
        dto.setPosition(employee.getPosition());
        dto.setStatus(employee.getStatus());
        dto.setCleanup(employee.getCleanup());
        dto.setSalary(employee.getSalary());
        dto.setAnnualLeave(employee.getAnnualLeave());
        dto.setHireDate(employee.getHireDate());
        return dto;
    }

    public void addFCMToken(String email, String fcmToken){
        Optional<Member> member = Optional.ofNullable(memberRepository.findByEmail(email));
        FcmTokenEntity token = new FcmTokenEntity(member.orElseThrow(), fcmToken);
        fcmTokenRepository.save(token);
    }

    // 이메일 중복 여부 체크
    public boolean isEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 전화번호 중복 여부 체크
    public boolean isPhoneExists(String phone) {
        return memberRepository.existsByPhone(phone);
    }

    // 메신저
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllChatableUsers(Long currentUserId) {
        return memberRepository.findAll().stream()
                .filter(member -> !member.getId().equals(currentUserId)) // 현재 사용자 제외
                .map(member -> {
                    MemberDTO dto = new MemberDTO();
                    dto.setId(member.getId());
                    dto.setName(member.getName());
                    if (member.getEmployees() != null) {
                        EmployeeDTO employeeDTO = covertToEmployeeDTO(member.getEmployees());
                        dto.setRole(member.getRole().name()); // Role 정보 설정
                        // 직위 정보는 별도의 필드나 메시지로 전달
                        dto.setMessage(employeeDTO.getPosition()); // MemberDTO에 메시지 필드 추가 필요
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<Member> findById(Long Id){
        return memberRepository.findById(Id);
    }



}
