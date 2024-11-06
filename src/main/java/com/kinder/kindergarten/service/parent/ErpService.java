package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ClassRoom;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.children.ChildrenRepository;
import com.kinder.kindergarten.repository.children.ClassRoomRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Builder
@Log4j2
public class ErpService {

    // Erp에서 직원이 학부모와 원아 데이터를 받아서 등록하기 위한 서비스

    private final ParentRepository parentRepository;
    private final ChildrenRepository childrenRepository;
    private final ClassRoomRepository classRoomRepository;

    public Long parentRegister(ParentErpDTO parentErpDTO) {
        // erp에서 직원이 학부모 등록하는 서비스 메서드

        if(parentErpDTO.getParentEmail() != null && !parentErpDTO.getParentEmail().isEmpty()) {
            // 이메일 중복 체크 (이메일이 입력된 경우에만)

            if(parentRepository.findByParentEmail(parentErpDTO.getParentEmail()).isPresent()) {
                //
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
                .parentPassword("tempPassword")  // ERP 등록시에는 임시 비밀번호 설정
                .isErpRegistered(true)  // ERP에서 등록됨을 표시
                .build();
                // 빌더를 이용해서 이름, 이메일, 폰번호, 주소, 긴급연락처 등 변환

        Parent savedParent = parentRepository.save(parent);
        // repository에 저장한 값을 savedParent변수에 저장한다.

        return savedParent.getParentId();
        //저장한 변수에다가 부모id에다가 리턴한다.
    }

    public void childrenRegister(List<ChildrenErpDTO> childrenList, Long parentId) {
        //erp에서 직원이 원아를 등록하는 서비스 메서드

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 원아의 부모를 찾을 수 없습니다."));
        // 유효성 검사 -> parentId로 레포지토리에서 해당 원아의 학부모 정보를 조회 -> 없으면 입센션

        for (ChildrenErpDTO childrenErpDTO :childrenList ) {

            ClassRoom classRoom = classRoomRepository.findByClassRoomName(childrenErpDTO.getClassName())
                    .orElseThrow(() -> new IllegalArgumentException("해당 원아의 반 정보를 찾을 수 없습니다."));
            // classRoomRepository 에서 className으로 조회

            if (classRoom.getCurrentStudents() >= classRoom.getMaxChildren()) {
                // 해당 원아의 반의 정원이 다 찼다면 ?

                throw new IllegalStateException(classRoom.getClassRoomName() + "의 정원이 초과 되었습니다.");
                // 정원이 초과되었다고 알린다.
            }

            Children children = Children.builder()
                    .childrenName(childrenErpDTO.getChildrenName())
                    .childrenBirthDate(childrenErpDTO.getChildrenBirthDate())
                    .childrenGender(childrenErpDTO.getChildrenGender())
                    .childrenBloodType(childrenErpDTO.getChildrenBloodType())
                    .assignedClass(classRoom)
                    .childrenAllergies(childrenErpDTO.getChildrenAllergies())
                    .childrenMedicalHistory(childrenErpDTO.getChildrenMedicalHistory())
                    .parent(parent)
                    .childrenNotes(childrenErpDTO.getChildrenNotes())
                    .build();
            // 원아에서도 빌드 패턴을 이용하여 원아의 이름, 생일, 성별, 혈액형 등 변환

            Children savedChildren = childrenRepository.save(children);
            log.info("원아 등록 서비스 시작 : " + savedChildren);
            // 자녀의 정보를 저장한다.

            // 반의 현재 원아 수 증가
           classRoom.setCurrentStudents(classRoom.getCurrentStudents() + 1);
            classRoomRepository.save(classRoom);
        }
    }
}
