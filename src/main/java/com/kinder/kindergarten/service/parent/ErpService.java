package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.children.ChildrenRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Builder
public class ErpService {

    // Erp에서 직원이 학부모와 원아 데이터를 받아서 등록하기 위한 서비스

    private final ParentRepository parentRepository;
    private final ChildrenRepository childrenRepository;

    public Long parentRegister(ParentErpDTO parentErpDTO) {
        // erp에서 직원이 학부모 등록하는 서비스 메서드

        if (parentRepository.findByParentEmail(parentErpDTO.getParentEmail()).isPresent()) {

            throw new IllegalStateException("이미 등록된 계정 입니다.");

        }

        // DTO -> Entity 변환
        Parent parent = Parent.builder()
                .parentName(parentErpDTO.getParentName())
                .parentEmail(parentErpDTO.getParentEmail())
                .parentPhone(parentErpDTO.getParentPhone())
                .parentAddress(parentErpDTO.getParentAddress())
                .childrenEmergencyPhone(parentErpDTO.getEmergencyContact())
                .childrenRole(parentErpDTO.getChildrenRole())
                .parentCreateDate(parentErpDTO.getParentCreateDate())
                .build();
                // 빌더를 이용해서 이름, 이메일, 폰번호, 주소, 긴급연락처 등 변환

        Parent savedParent = parentRepository.save(parent);
        // repository에 저장한 값을 savedParent변수에 저장한다.

        return savedParent.getParentId();
        //저장한 변수에다가 부모id에다가 리턴한다.
    }

    public void childrenRegister(ChildrenErpDTO childrenErpDTO) {
        //erp에서 직원이 원아를 등록하는 서비스 메서드

        Parent parent = parentRepository.findById(childrenErpDTO.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이의 부모를 찾을 수 없습니다."));

        Children children = Children.builder()
                .childrenName(childrenErpDTO.getChildrenName())
                .childrenBirthDate(childrenErpDTO.getChildrenBirthDate())
                .childrenGender(childrenErpDTO.getChildrenGender())
                .bloodType(childrenErpDTO.getChildrenBloodType())
                .assignedClass(childrenErpDTO.getClassName())
                .childrenAllergies(childrenErpDTO.getChildrenAllergies())
                .childrenMedicalHistory(childrenErpDTO.getChildrenMedicalHistory())
                .parent(parent)
                .childrenCreateDate(childrenErpDTO.getEnrollmentDate())
                .build();

        childrenRepository.save(children);
    }
}
