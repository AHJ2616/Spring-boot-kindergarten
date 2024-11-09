package com.kinder.kindergarten.service.children;

import com.kinder.kindergarten.DTO.children.ChildrenClassRoomDTO;
import com.kinder.kindergarten.DTO.children.ChildrenErpDTO;
import com.kinder.kindergarten.DTO.parent.ParentErpDTO;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ClassRoom;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.repository.children.ChildrenRepository;
import com.kinder.kindergarten.repository.children.ClassRoomRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ChildrenService {

    private final ParentRepository parentRepository;
    private final ChildrenRepository childrenRepository;

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

        return ChildrenErpDTO.builder()
                .childrenId(children.getChildrenId())
                .childrenName(children.getChildrenName())
                .childrenBirthDate(children.getChildrenBirthDate())
                .childrenGender(children.getChildrenGender())
                .build();
    }
}
