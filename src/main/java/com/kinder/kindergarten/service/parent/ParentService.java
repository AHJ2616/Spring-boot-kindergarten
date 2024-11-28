package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentUpdateDTO;
import com.kinder.kindergarten.DTO.parent.RegistrationCompletionDTO;
import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ClassRoom;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.children.ChildrenRepository;
import com.kinder.kindergarten.repository.children.ClassRoomRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class ParentService {

    private final ParentRepository parentRepository;

    private final MemberRepository memberRepository;

    private final ChildrenRepository childrenRepository;

    private final ClassRoomRepository classRoomRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Transactional
    public Long parentRegister(ParentErpDTO dto) {
        log.info("ParentService.parentRegister 메서드 실행 중  = = = = = =", dto);

        try {
            // 1. 이메일 중복 체크
            if (memberRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalStateException("이미 등록된 이메일입니다.");
            }

            // 2. Member 엔티티 생성 및 저장
            String tempPassword = tempPassWordMake();
            Member member = Member.builder()
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .address(dto.getAddress())
                    .password(passwordEncoder.encode(tempPassword))
                    .role(Role.ROLE_PARENT)
                    .build();

            memberRepository.save(member);
            // 1번, 2번 데이터를 DB에 저장!

            log.info("Member 이메일로 저장한 값 : " + member.getEmail());

            // 3. Parent 엔티티 생성 및 저장
            Parent parent = Parent.builder()
                    .memberEmail(member.getEmail())
                    .childrenEmergencyPhone(dto.getChildrenEmergencyPhone())
                    .parentType(dto.getParentType())
                    .registrationStatus(RegistrationStatus.PENDING)
                    .build();

            log.info("비상연락처 값이 넘어옵니까?!", dto.getChildrenEmergencyPhone());

            Parent savedParent = parentRepository.save(parent);
            log.info("학부모 ID + 비상연락처 데이터 확인하긔 : " + savedParent.getParentId(), savedParent.getChildrenEmergencyPhone());  // 저장된 데이터 확인 로그

            // 4. 임시 비밀번호 설정
            dto.setTempPassword(tempPassword);

            return savedParent.getParentId();

        } catch (Exception e) {
            log.error("Error during parent registration: ", e);
            throw new RuntimeException("학부모 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String tempPassWordMake() {
        // 임시 비밀번호 생성해주는 서비스 메서드(ERP의 직원이 학부모 등록하기 과정)
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upper + lower + digits + specialChars;
        // 임시 비밀번호에다가 지정할 대소문자와 숫자(0~9), 특수 문자를 정해준다!

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        // 대소문자, 숫자, 특수문자의 각 문자 타입에서 최소 1개씩 선택

        // 나머지 4자리를 랜덤하게 추가
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 생성된 비밀번호를 섞기
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        log.info("임시 비밀번호 생성 완료");
        return new String(passwordArray);
        // 배열의 문자열 리턴한다.

        /* for문을 이용하여 Fisher-Yates 셔플 알고리즘을 사용하여 생성된 비밀번호를 무작위로 섞는다.
        섞는 이유는 : 비밀번호를 생성할 때 대문자, 소문자, 숫자, 특수문자 순서대로 추가했기 때문에 패턴이 생길 수 있기 때문에  모든 문자의 위치를 섞는다.

        여기서, Fisher-Yates 셔플 알고리즘이란?
        배열, 리스트의 요소들을 무작위로 섞어주면서 공정한 무작위성을 보장하면서도 성능이 뛰어난 특징이 있다.
        주로 카드 게임 덱, 랜덤 배열 생성할 때 자주 사용된다.
         */
    }

    public Page<ParentErpDTO> getAllParents(Pageable pageable) {
        // 등록된 모든 학부모의 데이터를 끌고 와서 페이징 처리 해주는 서비스 메서드

        Page<Parent> parentPage = parentRepository.findAll(pageable);
        // 1. DB에서 모든 정보를 끌고와서 페이지 변수 지정

        return parentPage.map(parent -> {
            Member member = memberRepository.findByEmail(parent.getMemberEmail());
            ParentErpDTO dto = modelMapper.map(parent, ParentErpDTO.class);

            // Member 정보 설정
            if (member != null) {
                dto.setEmail(member.getEmail());
                dto.setName(member.getName());
                dto.setPhone(member.getPhone());
                dto.setAddress(member.getAddress());
            }

            dto.setChildrenIds(parent.getChildren().stream()
                    .map(child -> modelMapper.map(child, ChildrenErpDTO.class))
                    .collect(Collectors.toList()));

            return maskPersonalInfo(dto);
        });
    }

    public ParentErpDTO getParentDetail(Long parentId) {
        // 등록된 학부모의 상세보기 서비스 메서드

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모 정보를 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(parent.getMemberEmail());

        ParentErpDTO dto = new ParentErpDTO();

        // Member 정보 설정
        if (member != null) {
            dto.setName(member.getName());
            dto.setEmail(member.getEmail());
            dto.setPhone(member.getPhone());
            dto.setAddress(member.getAddress());
        }

        // Parent 정보 설정
        dto.setParentId(parent.getParentId());
        dto.setChildrenEmergencyPhone(parent.getChildrenEmergencyPhone());
        dto.setParentType(parent.getParentType());

        // 날짜 정보 설정
        if (parent.getCreatedDate() != null) {
            dto.setParentCreateDate(parent.getCreatedDate().toLocalDate());
        }
        if (parent.getUpdatedDate() != null) {
            dto.setParentModifyDate(parent.getUpdatedDate().toLocalDate());
        }

        // 자녀 정보 설정
        dto.setChildrenIds(parent.getChildren().stream()
                .map(child -> modelMapper.map(child, ChildrenErpDTO.class))
                .collect(Collectors.toList()));

        return maskPersonalInfo(dto);
    }

    public Page<ParentErpDTO> findByNameContaining(String keyword, Pageable pageable) {
        try {
            Page<Member> members = memberRepository.findByNameContainingAndRole(keyword, Role.ROLE_PARENT, pageable);
            return members.map(member -> {
                Parent parent = parentRepository.findByMemberEmail(member.getEmail())
                        .orElseThrow(() -> new EntityNotFoundException("Parent not found for email: " + member.getEmail()));

                ParentErpDTO dto = ParentErpDTO.builder()
                        .parentId(parent.getParentId())
                        .name(member.getName())
                        .email(member.getEmail())
                        .phone(member.getPhone())
                        .address(member.getAddress())
                        .childrenEmergencyPhone(parent.getChildrenEmergencyPhone())
                        .parentType(parent.getParentType())
                        .build();

                if (parent.getCreatedDate() != null) {
                    dto.setParentCreateDate(parent.getCreatedDate().toLocalDate());
                }
                if (parent.getUpdatedDate() != null) {
                    dto.setParentModifyDate(parent.getUpdatedDate().toLocalDate());
                }

                return maskPersonalInfo(dto);
            });
        } catch (Exception e) {
            log.error("이름 검색 중 오류 발생: ", e);
            throw new RuntimeException("검색 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void updateParent(ParentUpdateDTO updateDTO) {
        // 학부모의 정보를 수정하는 서비스 메서드

        try {
            // Parent 엔티티 조회
            Parent parent = parentRepository.findById(updateDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("학부모 정보를 찾을 수 없습니다."));

            // Member 엔티티 조회
            Member member = memberRepository.findByEmail(parent.getMemberEmail());
            if (member == null) {
                throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다.");
            }

            // Member 정보 업데이트
            member.setName(updateDTO.getName());
            member.setPhone(updateDTO.getPhone());
            member.setAddress(updateDTO.getAddress());
            memberRepository.save(member);
            log.info("Member 정보 업데이트 완료: {}", member.getEmail());

            // Parent 정보 업데이트
            parent.setChildrenEmergencyPhone(updateDTO.getChildrenEmergencyPhone());
            parent.setParentType(updateDTO.getParentType());
            parentRepository.save(parent);
            log.info("Parent 정보 업데이트 완료: {}", parent.getParentId());

        } catch (Exception e) {
            log.error("학부모 정보 수정 중 오류 발생: ", e);
            throw new RuntimeException("학부모 정보 수정에 실패했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteParent(Long parentId) {
        // 학부모의 ID를 이용하여 삭제하는 서비스 메서드

        try {
            // Parent 엔티티 조회
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("삭제할 학부모 정보를 찾을 수 없습니다. ID: " + parentId));

            // Member 엔티티 조회
            Member member = memberRepository.findByEmail(parent.getMemberEmail());
            if (member == null) {
                throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다. Email: " + parent.getMemberEmail());
            }

            log.info("학부모 삭제 시작 - ParentID: {}, MemberEmail: {}", parentId, parent.getMemberEmail());

            // Parent 엔티티 삭제
            parentRepository.delete(parent);
            log.info("Parent 정보 삭제 완료");

            // Member 엔티티 삭제
            memberRepository.delete(member);
            log.info("Member 정보 삭제 완료");

        } catch (Exception e) {
            log.error("학부모 정보 삭제 중 오류 발생: ", e);
            throw new RuntimeException("학부모 정보 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    public ParentErpDTO maskPersonalInfo(ParentErpDTO dto) {
        /* 학부모 데이터의 마스킹 처리 하는 서비스 메서드
        여기서, 마스킹(Masking) 처리란? 민감한 정보(개인정보, 금융정보 등)가 외부에 노출되지 않도록 일부 데이터를 숨기거나 가리는 기술을 의마함.
        데이터를 완전히 가리는게 아닌, 특정 부분만 가려서 사용자가 데이터를 식별할 수 없도록 처리하는 방식

       11.25 추가
         */


        try {
            if (dto.getName() != null && dto.getName().length() >= 2) {

                dto.setName(dto.getName().charAt(0) + "*" + dto.getName().substring(dto.getName().length() - 1));
            }// 성함 데이터 마스킹

            if (dto.getPhone() != null) {
                String phone = dto.getPhone().replaceAll("[^0-9]", ""); // 숫자만 추출
                if (phone.length() == 11) { // 휴대폰 번호 형식(11자리)
                    dto.setPhone(phone.substring(0, 3) + "-****-" + phone.substring(7));
                }
            }// 연락처 데이터 마스킹

            if (dto.getChildrenEmergencyPhone() != null) {
                String emergencyPhone = dto.getChildrenEmergencyPhone().replaceAll("[^0-9]", "");
                if (emergencyPhone.length() == 11) {
                    dto.setChildrenEmergencyPhone(emergencyPhone.substring(0, 3) + "-****-" +
                            emergencyPhone.substring(7));
                }
            }// 비상 연락처 마스킹

            if (dto.getAddress() != null) {

                String[] addressParts = dto.getAddress().split(" ");

                if (addressParts.length > 2) {
                    dto.setAddress(addressParts[0] + " " + addressParts[1] + " *****");
                }
            }// 주소 마스킹

            return dto;
        } catch (Exception e) {
            log.error("마스킹 처리 중 오류 발생 !" , e);
            return dto;
        }
    }

    @Transactional
    public void approveParent(Long parentId, String approvedBy) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모 정보를 찾을 수 없습니다."));

        parent.setRegistrationStatus(RegistrationStatus.APPROVED);
        parent.setApprovedAt(LocalDateTime.now());
        parent.setApprovedBy(approvedBy);

        log.info("학부모 승인 완료 - ID: {}, 승인자: {}", parentId, approvedBy);
    }

    @Transactional
    public void rejectParent(Long parentId, String rejectReason, String approvedBy) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모 정보를 찾을 수 없습니다."));

        parent.setRegistrationStatus(RegistrationStatus.REJECTED);
        parent.setRejectReason(rejectReason);
        parent.setApprovedAt(LocalDateTime.now());
        parent.setApprovedBy(approvedBy);

        log.info("학부모 반려 완료 - ID: {}, 사유: {}, 처리자: {}",
                parentId, rejectReason, approvedBy);
    }

    // 대기 중인 학부모 목록 조회
    public Page<ParentErpDTO> getPendingRegistrations(Pageable pageable) {
        try {
            Page<Parent> pendingParents = parentRepository.findByRegistrationStatus(
                    RegistrationStatus.PENDING,
                    pageable
            );

            return pendingParents.map(parent -> {
                Member member = memberRepository.findByEmail(parent.getMemberEmail());
                if (member == null) {
                    log.error("Member not found for email: {}", parent.getMemberEmail());
                    return null;
                }
                ParentErpDTO dto = convertToDTO(parent, member);
                return maskPersonalInfo(dto);
            });
        } catch (Exception e) {
            log.error("대기 중인 학부모 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 승인된 학부모 목록 조회
    public Page<ParentErpDTO> getApprovedParents(Pageable pageable) {
        try {
            Page<Parent> approvedParents = parentRepository.findByRegistrationStatus(
                    RegistrationStatus.APPROVED,
                    pageable
            );

            return approvedParents.map(parent -> {
                Member member = memberRepository.findByEmail(parent.getMemberEmail());
                if (member == null) {
                    log.error("Member not found for email: {}", parent.getMemberEmail());
                    return null;
                }
                ParentErpDTO dto = convertToDTO(parent, member);
                return maskPersonalInfo(dto);
            });
        } catch (Exception e) {
            log.error("승인된 학부모 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 반려된 학부모 목록 조회
    public Page<ParentErpDTO> getRejectedParents(Pageable pageable) {
        try {
            Page<Parent> rejectedParents = parentRepository.findByRegistrationStatus(
                    RegistrationStatus.REJECTED,
                    pageable
            );

            return rejectedParents.map(parent -> {
                Member member = memberRepository.findByEmail(parent.getMemberEmail());
                if (member == null) {
                    log.error("Member not found for email: {}", parent.getMemberEmail());
                    return null;
                }
                ParentErpDTO dto = convertToDTO(parent, member);
                return maskPersonalInfo(dto);
            });
        } catch (Exception e) {
            log.error("반려된 학부모 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("목록 조회 중 오류가 발생했습니다.");
        }
    }

    public RegistrationCompletionDTO getRegistrationCompletionInfo(Long parentId,
                                                                   Long childrenId,
                                                                   Long classRoomId) {

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모 정보를 찾을 수 없습니다."));

        Children children = childrenRepository.findById(childrenId)
                .orElseThrow(() -> new EntityNotFoundException("원아 정보를 찾을 수 없습니다."));

        ClassRoom classRoom = classRoomRepository.findById(classRoomId)
                .orElseThrow(() -> new EntityNotFoundException("반 정보를 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(parent.getMemberEmail());

        return RegistrationCompletionDTO.builder()
                .memberEmail(member.getEmail())
                .memberName(member.getName())
                .memberPhone(member.getPhone())
                .memberAddress(member.getAddress())
                .parentId(parent.getParentId())
                .childrenEmergencyPhone(parent.getChildrenEmergencyPhone())
                .detailAddress(parent.getDetailAddress())
                .parentType(parent.getParentType())
                .childrenId(children.getChildrenId())
                .childrenName(children.getChildrenName())
                .childrenGender(children.getChildrenGender())
                .childrenBirthDate(children.getChildrenBirthDate())
                .childrenAllergies(children.getChildrenAllergies())
                .childrenMedicalHistory(children.getChildrenMedicalHistory())
                .classRoomId(classRoom.getClassRoomId())
                .classRoomName(classRoom.getClassRoomName())
                .employeeName(classRoom.getEmployeeName())
                .currentStudents(classRoom.getCurrentStudents())
                .maxChildren(classRoom.getMaxChildren())
                .registrationDate(LocalDateTime.now())
                .build();

    }

    public ByteArrayResource generateExcelFile(Long parentId, Long childrenId) throws IOException {
        // 데이터 조회
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모 정보를 찾을 수 없습니다."));
        Children children = childrenRepository.findById(childrenId)
                .orElseThrow(() -> new EntityNotFoundException("원아 정보를 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(parent.getMemberEmail());

        // 엑셀 워크북 생성
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("등록 정보");

            // 스타일 설정
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 데이터 입력
            int rowNum = 0;

            // 회원 정보
            createHeaderRow(sheet, rowNum++, "회원 정보", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "이름", member.getName());
            rowNum = addDataRow(sheet, rowNum, "이메일", member.getEmail());
            rowNum = addDataRow(sheet, rowNum, "연락처", member.getPhone());

            // 학부모 정보
            rowNum++;
            createHeaderRow(sheet, rowNum++, "학부모 정보", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "긴급연락처", parent.getChildrenEmergencyPhone());
            rowNum = addDataRow(sheet, rowNum, "관계", parent.getParentType().toString());

            // 원아 정보
            rowNum++;
            createHeaderRow(sheet, rowNum++, "원아 정보", headerStyle);
            rowNum = addDataRow(sheet, rowNum, "이름", children.getChildrenName());
            rowNum = addDataRow(sheet, rowNum, "생년월일", children.getChildrenBirthDate().toString());
            rowNum = addDataRow(sheet, rowNum, "성별", children.getChildrenGender());
            rowNum = addDataRow(sheet, rowNum, "알레르기", children.getChildrenAllergies());

            // 엑셀 파일을 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    // 헤더 셀 생성 헬퍼 메서드
    private void createHeaderRow(Sheet sheet, int rowNum, String title, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);
    }

    // 데이터 행 추가 헬퍼 메서드
    private int addDataRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value != null ? value : "");
        return rowNum + 1;
    }

    private ParentErpDTO convertToDTO(Parent parent, Member member) {
        if (parent == null || member == null) return null;

        return ParentErpDTO.builder()
                .parentId(parent.getParentId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .childrenEmergencyPhone(parent.getChildrenEmergencyPhone())
                .parentType(parent.getParentType())
                .registrationStatus(parent.getRegistrationStatus())
                .rejectReason(parent.getRejectReason())
                .approvedAt(parent.getApprovedAt())
                .approvedBy(parent.getApprovedBy())
                .parentCreateDate(parent.getCreatedDate().toLocalDate())
                .parentModifyDate(parent.getUpdatedDate().toLocalDate())
                .build();
    }
}
