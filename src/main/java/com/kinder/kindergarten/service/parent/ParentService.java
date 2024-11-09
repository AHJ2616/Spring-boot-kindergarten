package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    public Long parentRegister(ParentErpDTO parentErpDTO) {
        // erp에서 직원이 학부모 등록하는 서비스 메서드

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
                .isErpRegistered(true)  // ERP를 통한 등록임을 표시
                .build();

        Parent savedParent = parentRepository.save(parent);
        // savedParent으로 변환한 값들을 레포지토리에 저장하고 DB로 전달

        return savedParent.getParentId();
    }
}
