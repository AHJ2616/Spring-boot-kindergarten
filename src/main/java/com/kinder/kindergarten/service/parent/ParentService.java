package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ParentService {

    private final ParentRepository parentRepository;

    private final PasswordEncoder passwordEncoder;

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
                .childrenEmergencyPhone(parentErpDTO.getEmergencyContact())
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


}
