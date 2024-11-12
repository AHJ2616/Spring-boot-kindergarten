package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentUpdateDTO;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class ParentService {

    private final ParentRepository parentRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    public Long parentRegister(ParentErpDTO parentErpDTO) {
        // erp에서 직원이 학부모 등록하는 서비스 메서드

        String tempPassword = tempPassWordMake();
        //  임시 비밀번호 조합을 만든 메서드를 tempPassWord에다가 넣는다.
        log.info("Generated temporary password: {}", tempPassword);


        parentErpDTO.setParentPassword(tempPassword);
        // DTO에다가 임시 비밀번호를 주입한다.
        log.info("Set temporary password to DTO: {}", parentErpDTO.getTempPassword());


        String encodedPassword = passwordEncoder.encode(tempPassword);
        parentErpDTO.setParentPassword(encodedPassword);
        // 임시 비밀번호를 암호화 시킨 값을 DTO에다가 주입 한다.

        // 이메일 중복 체크 (이메일이 입력된 경우에만)
        if(parentErpDTO.getParentEmail() != null && !parentErpDTO.getParentEmail().isEmpty()) {

            if(parentRepository.findByParentEmail(parentErpDTO.getParentEmail()).isPresent()) {
                // 중복된 이메일이 입력이 될 경우 메세지를 알려준다.
                throw new IllegalStateException("이미 등록된 이메일입니다.");
            }
        }

        // DTO -> Entity 변환
        Parent parent = Parent.builder()
                .parentName(parentErpDTO.getParentName())
                .parentEmail(parentErpDTO.getParentEmail())
                .parentPhone(parentErpDTO.getParentPhone())
                .parentAddress(parentErpDTO.getParentAddress())
                .childrenEmergencyPhone(parentErpDTO.getChildrenEmergencyPhone())
                .parentType(parentErpDTO.getParentType())
                .parentPassword(encodedPassword) // 암호화된 비밀번호 저장
                .isErpRegistered(true)  // ERP를 통한 등록임을 표시
                .build();

        Parent savedParent = parentRepository.save(parent);
        // savedParent으로 변환한 값들을 레포지토리에 저장하고 DB로 전달


        // 저장 후 다시 한번 임시 비밀번호 설정 (혹시 모를 초기화 방지)
        parentErpDTO.setTempPassword(tempPassword);
        log.info("Final check of temporary password: {}", parentErpDTO.getTempPassword());

        return savedParent.getParentId();
    }

    private String tempPassWordMake() {
        // 임시 비밀번호 생성하는 메서드

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";    //대문자
        String lower = upper.toLowerCase(); //소문자
        String digits = "0123456789";   // 0~9 숫자
        String specialChars = "!@#$%^&*";   // 특수문자

        String allChars = upper + lower + digits + specialChars;//
        // 모든 사용할 문자들을 하나로 만든다.

        SecureRandom random = new SecureRandom();
        // 일반 랜덤보다 안전한 SecureRandom 라는 변수를 이용해서 랜덤 생성한다.

        StringBuilder password = new StringBuilder();
        // 비밀번호를 담아줄 집을 생성해준다.

        password.append(upper.charAt(random.nextInt(upper.length())));// 대문자 1개
        password.append(lower.charAt(random.nextInt(lower.length())));  // 소문자 1개
        password.append(digits.charAt(random.nextInt(digits.length())));    // 숫자 1개
        password.append(specialChars.charAt(random.nextInt(specialChars.length()))); // 특수문자 1개
        // 최소한 비밀번호 설정을 위해 각 문자에서 하나씩 선택하게 한다. (4자리)

        for (int i = 0; i < 4; i++ ) {
            // 나머지 4자리를 랜덤하게 추가한다.

            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        // 패스워드에 문자열 배열을 지정해준다.

        for (int i = passwordArray.length -1; i > 0; i--) {
            // 0에서 i 사이의 인덱스를 무작위로 선택하고

            int j = random.nextInt(i + 1);// // 현재 위치(i)의 문자와 랜덤하게 선택된 위치(j)의 문자를 교환
            char temp = passwordArray[i];

            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        /* for문을 이용하여 Fisher-Yates 셔플 알고리즘을 사용하여 생성된 비밀번호를 무작위로 섞는다.
        섞는 이유는 : 비밀번호를 생성할 때 대문자, 소문자, 숫자, 특수문자 순서대로 추가했기 때문에 패턴이 생길 수 있기 때문에  모든 문자의 위치를 섞는다.

        여기서, Fisher-Yates 셔플 알고리즘이란?
        배열, 리스트의 요소들을 무작위로 섞어주면서 공정한 무작위성을 보장하면서도 성능이 뛰어난 특징이 있다.
        주로 카드 게임 덱, 랜덤 배열 생성할 때 자주 사용된다.
         */

        return new String(passwordArray);
        // 배열의 문자열 리턴한다.
    }

    public Page<ParentErpDTO> getAllParents(Pageable pageable) {
        // 등록된 부모 조회(리스트) 하는 메서드

        Page<Parent> parentPage = parentRepository.findAll(pageable);
        // 레포지토리에서 등록된 학부모의 모든 정보를 다 가져오고 PAGE 처리 한다.

        return parentPage.map(parent -> modelMapper.map(parent, ParentErpDTO.class));
        // modelMapper으로 맵 형식으로 학부모의 정보와 DTO를 리턴한다.
    }

    public ParentErpDTO getParentDetail(Long parentId) {
        // 학부모의 상세 보기 기능을 하는 서비스 메서드

        Parent parent = parentRepository.findById(parentId).orElseThrow(() ->
                new EntityNotFoundException("학부모의 정보를 찾을 수 없습니다."));
        // 레포지토리에서 학부모의 ID를 이용하여 학부모의 정보를 가져오고 정보가 없으면 메세지를 출력한다.

        return modelMapper.map(parent, ParentErpDTO.class);
        // modelMapper으로 맵 형식으로 학부모의 정보와 DTO를 리턴한다.
    }

    public Page<ParentErpDTO> searchParents(String keyword, Pageable pageable) {
        // 등록된 학부모 조회할 때 검색창에서 학부모 검색하는 서비스 메서드

        Page<Parent> parentPage = parentRepository.findByParentNameContaining(keyword, pageable);
        // 학부모의 엔티티를 Page 처리로 만들고 레포지토리에서 부모의 이름으로 검색하는 쿼리메서드를 가져온다.

        return parentPage.map(parent -> modelMapper.map(parent, ParentErpDTO.class));
        // modelMapper로 이용해서 맵형식으로 학부모의 엔티티 + DTO를 리턴한다.
    }

    @Transactional
    public void updateParent(ParentUpdateDTO parentUpdateDTO) {
        // 학부모 정보 수정하는 서비스 메서드

        log.info("ParentService.updateParent 메서드 실행 중   - - - -" + parentUpdateDTO);

            Parent parent = parentRepository.findById(parentUpdateDTO.getParentId()).orElseThrow(() ->
                    new EntityNotFoundException("학부모의 정보를 찾을 수 없습니다."));
            // 레포지토리에 학부모의 ID를 이용해서 학부모의 정보를 찾으며 없으면 메세지를 보낸다.

        log.info("부모 객체 : " + parent);

            parent.setParentName(parentUpdateDTO.getParentName());
            parent.setParentPhone(parentUpdateDTO.getParentPhone());
            parent.setChildrenEmergencyPhone(parentUpdateDTO.getChildrenEmergencyPhone());
            parent.setParentAddress(parentUpdateDTO.getParentAddress());
            parent.setParentType(parentUpdateDTO.getParentType());
            // Parent 엔티티의 성함, 연락처, 긴급연락처, 주소, 법정보호자인 필드를 parentUpdateDTO에다가 설정한다.

        log.info("업데이트된 Parent 엔티티 : " + parent);

        Parent savedParent = parentRepository.save(parent);
        //수정한 데이터를 레포지토리를 통하여 DB로 보내고 savedParent변수에 저장한다.

        log.info("저장된 Parent 엔티티 : " + savedParent);

    }

    @Transactional
    public void deleteParent(Long parentId) {
        // 학부모 데이터를 삭제하는 서비스 메서드

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모의 정보를 찾을 수 없습니다. ID :" + parentId));
        // 레포지토리에서 부모의ID를 찾는 검사를 한다. 없으면 메세지를 보낸다.

        parentRepository.delete(parent);
        // 삭제한 부모를 DB에 전달
    }


    private ParentErpDTO convertToDTO(Parent parent) {
        return ParentErpDTO.builder()
                .parentId(parent.getParentId())
                .parentName(parent.getParentName())
                .parentEmail(parent.getParentEmail())
                .parentPhone(parent.getParentPhone())
                .parentAddress(parent.getParentAddress())
                .childrenEmergencyPhone(parent.getChildrenEmergencyPhone())
                .parentType(parent.getParentType())
                .parentCreateDate(parent.getCreatedDate().toLocalDate())
                .parentModifyDate(parent.getUpdatedDate().toLocalDate())
                .build();
    }
}
