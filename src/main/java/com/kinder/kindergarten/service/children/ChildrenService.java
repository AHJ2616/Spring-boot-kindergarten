package com.kinder.kindergarten.service.children;

import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.children.ChildrenUpdateDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.children.ChildrenRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ChildrenService {

    private final ParentRepository parentRepository;
    private final ChildrenRepository childrenRepository;

    private final MemberRepository memberRepository;

    public List<Long> registerChildren(List<ChildrenErpDTO> childrenDTOs, Long parentId) {
        // 원아 등록하기를 위한 서비스 메서드

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("학부모를 찾을 수 없습니다."));
        // 먼저, parentId로 부모 정보를 검색한다. 없으면 입센션 발생

        List<Long> childrenIds = new ArrayList<>();
        // childrenIds 라는 리스트를 만들어준다.

        for (ChildrenErpDTO dto : childrenDTOs) {

            Children children = Children.builder()
                    .childrenName(dto.getChildrenName())
                    .childrenBirthDate(dto.getChildrenBirthDate())
                    .childrenGender(dto.getChildrenGender())
                    .childrenBloodType(dto.getChildrenBloodType())
                    .childrenAllergies(dto.getChildrenAllergies())
                    .childrenMedicalHistory(dto.getChildrenMedicalHistory())
                    .childrenNotes(dto.getChildrenNotes())
                    .parent(parent)
                    .build();
            // ChildrenErpDTO를 For문으로 돌려서 builder로 이용하여 원아의 정보를 입력한다.

            log.info("저장된 원아 객체 : " + children);

            Children savedChildren = childrenRepository.save(children);
            // 저장된 원아 객체를 Repository에 저장해서 DB로 전달

            childrenIds.add(savedChildren.getChildrenId());
            // 저장된 원아를 원아ID로 LIST 변수명인 childrenIds 추가해준다.


            log.info("저장된 원아ID" + savedChildren.getChildrenId());
        }

        return childrenIds;
    }

    public List<ChildrenErpDTO> getChildrenByParentId(Long parentId) {
        // parentId 으로 부모의 모든 자녀를 조회하는 메서드

        List<Children> children = childrenRepository.findByParent_ParentId(parentId);

        return children.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ChildrenErpDTO convertToDTO(Children children) {
        // Children 엔티티를 DTO로 변환하는 메서드

        // Parent와 연관된 Member 정보 가져오기
        String parentName = "*미 등 록*";
        if (children.getParent() != null && children.getParent().getMemberEmail() != null) {
            Member parentMember = memberRepository.findByEmail(children.getParent().getMemberEmail());
            if (parentMember != null) {
                parentName = parentMember.getName();
            }
        }

        ChildrenErpDTO dto = ChildrenErpDTO.builder()
                .childrenId(children.getChildrenId())                // 원아ID
                .childrenName(children.getChildrenName())            // 원아 이름
                .childrenBirthDate(children.getChildrenBirthDate()) // 원아 생년월일
                .maskedBirthDate(children.getChildrenBirthDate().toString().substring(0, 4) +  "-**-**")// 마스킹된 생년월일
                .childrenGender(children.getChildrenGender())        // 원아 성별
                .enrollmentDate(children.getCreatedDate().toLocalDate())      // 원아 생성일
                .childrenModifyDate(children.getUpdatedDate().toLocalDate()) // 원아 수정일
                .childrenBloodType(children.getChildrenBloodType())  // 원아 혈액형
                .childrenAllergies(children.getChildrenAllergies())  // 원아 알레르기
                .childrenMedicalHistory(children.getChildrenMedicalHistory()) // 원아 병력정보
                .childrenNotes(children.getChildrenNotes())          // 원아 특이사항
                .classRoomName(children.getAssignedClass() != null ?
                        children.getAssignedClass().getClassRoomName() : "*미 배 정*")
                .parentName(parentName)  // 학부모 이름 (Member 엔티티에서 가져옴)
                .employeeName(children.getAssignedClass() != null &&
                        children.getAssignedClass().getEmployeeName() != null ?
                        children.getAssignedClass().getEmployeeName() : "미 등 록")
                .build();

        // 마스킹 처리 적용 (이름만 마스킹)
        if (dto.getChildrenName() != null && dto.getChildrenName().length() >= 2) {
            dto.setChildrenName(dto.getChildrenName().charAt(0) + "*" +
                    dto.getChildrenName().substring(dto.getChildrenName().length() - 1));
        }

        return dto;
    }

    public List<ChildrenErpDTO> getAllChildren() {

        return childrenRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
        // 레포지토리에서 모든 정보 데이터를 끌고와서 MAP형식으로 모은다음 리스트 로 만든다.
    }

    public ChildrenErpDTO getChildrenById(Long childrenId) {
        // 원아의 ID를 가져오는 서비스 메서드

        Children children = childrenRepository.findById(childrenId).orElseThrow(()
        -> new EntityNotFoundException("원아를 찾을 수 없습니다."));

        return convertToDTO(children);
    }

    public Page<ChildrenErpDTO> getChildrenList(String searchType, String keyword, int page) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<Children> childrenPage;

        if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {

            switch (searchType) {
                case "name" :
                    childrenPage = childrenRepository.findByChildrenNameContaining(keyword, pageable);
                    break;

                case "class" :
                    childrenPage = childrenRepository.findByAssignedClass_ClassRoomNameContaining(keyword, pageable);
                    break;

                case "parent" :
                    childrenPage = childrenRepository.findByParentNameContaining(keyword, pageable);
                    break;

                default:
                    childrenPage = childrenRepository.findAll(pageable);
            }// switch END
            // switch를 오랜만에 활용하면서 이름, 반, 부모, 모두 경우로 나누어서 검색 타입을 설정

        } else {
            childrenPage = childrenRepository.findAll(pageable);
        }

        return childrenPage.map(this::convertToDTO);

    }

    @Transactional
    public void updateChildren(Long childrenId, ChildrenUpdateDTO updateDTO) {

        Children children = childrenRepository.findById(childrenId).orElseThrow(()->
                new EntityNotFoundException("원아를 찾을 수 없습니다."));
        // 레포지토리에서 원아의 ID를 찾아본다.

        children.setChildrenName(updateDTO.getChildrenName());
        children.setChildrenBirthDate(updateDTO.getChildrenBirthDate());
        children.setChildrenBloodType(updateDTO.getChildrenBloodType());
        children.setChildrenGender(updateDTO.getChildrenGender());
        children.setChildrenAllergies(updateDTO.getChildrenAllergies());
        children.setChildrenMedicalHistory(updateDTO.getChildrenMedicalHistory());
        children.setChildrenNotes(updateDTO.getChildrenNotes());
        // 업데이트 할 원아의 이름, 새연ㄴ월일, 혈액형, 성별, 알레르기, 의료정보, 기타사항을 지정해준다.

        childrenRepository.save(children);
        // 이러한 수정 사항을 레토지토리를 통해 DB에 저장한다.
    }

    @Transactional
    public void deleteChildren(Long childrenId) {
        // 등록된 원아를 삭제하는 서비스 메서드

        Children children = childrenRepository.findById(childrenId).orElseThrow(
                ()-> new EntityNotFoundException("해당 원아를 찾을 수 없습니다."));
        // 원아의 ID로 원아의 정보를 검색한다. 없으면 입셉션 발동

        if (children.getAssignedClass() != null) {
            children.setAssignedClass(null);
        }

        childrenRepository.delete(children);

    }

    public ChildrenErpDTO maskPersonalInfo(ChildrenErpDTO dto) {
        // 원아 데이터의 마스킹 처리 하는 서비스 메서드

        if (dto == null) return null;

        try {
            if (dto.getChildrenName() != null && dto.getChildrenName().length() >=2) {
                dto.setChildrenName(dto.getChildrenName().charAt(0) + "*" + dto.getChildrenName().substring(dto.getChildrenName().length() -1));
            }// 자녀 이름 마스킹 처리

            if (dto.getChildrenBirthDate() != null) {
                String birthYearStr = dto.getChildrenBirthDate().toString();
                String maskedDate = birthYearStr.substring(0, 4) + "-**-**";

                dto.setChildrenBirthDate(null);
                dto.setMaskedBirthDate(maskedDate);
            }// 자녀의 생년월일 마스킹 처리
            return dto;
        } catch (Exception e) {
            log.error("원아, 마스킹 처리 중 오류 발생 !", e);

            return dto;
        }
    }
}
