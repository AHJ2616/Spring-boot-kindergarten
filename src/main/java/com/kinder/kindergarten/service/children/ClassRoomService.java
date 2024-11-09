package com.kinder.kindergarten.service.children;

import com.kinder.kindergarten.DTO.children.ClassRoomDTO;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ClassRoom;
import com.kinder.kindergarten.repository.children.ChildrenRepository;
import com.kinder.kindergarten.repository.children.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ClassRoomService {

    // ClassRoom의 Service

    private final ClassRoomRepository classRoomRepository;

    private final ChildrenRepository childrenRepository;

    public ClassRoom createClassRoom(ClassRoomDTO classRoomDTO) {
        // ClassRoom 서비스에서 반 개설하는 메서드

        if (classRoomRepository.existsByClassRoomName(classRoomDTO.getClassRoomName())) {
            // 반 개설하기 전에 이름이 중복되는지 검사 한다.

            throw new IllegalStateException("이미 존재하는 반 이름입니다.");
            // 존재하는 반 이름이 있으면 입센션으로 알려준다.
        }

        // DTO를 엔티티로 변환
        ClassRoom classRoom = ClassRoom.builder()
                .classRoomName(classRoomDTO.getClassRoomName())
                .maxChildren(classRoomDTO.getMaxChildren())
                .classRoomDescription(classRoomDTO.getClassRoomDescription())
                .employeeName(classRoomDTO.getEmployeeName())
                .currentStudents(0)
                .build();

        // builder 패턴을 이용해서 반 이름, 정원 수, 반 설명, 담당 교사  등 정보들을 변환한다.
        log.info("반 개설 : " + classRoom);

        return classRoomRepository.save(classRoom);
    }


    public List<ClassRoomDTO> getAvailableClassRooms() {
        // 원아가 배정이 가능한 반이 있는지 조회하는 메서드

        List<ClassRoom> classRooms = classRoomRepository.findAll();
        // DB에 있는 반 목록을 모두 조회한다.

        return classRooms.stream().filter(this::hasAvailableMaxChildren) .map(this::convertToDTO)
                .collect(Collectors.toList());
        // 필터를 통해서 배정이 가능한 반으로 골라내고 DTO로 변환한 데이터를 LIST로 모은다.
    }


    public void assignChildToClassRoom(Long childrenId, Long classRoomId) {
        // 원아를 배정하고 싶은 반에 배정하는 메서드

        Children children = childrenRepository.findById(childrenId)
                .orElseThrow(() -> new IllegalArgumentException("원아를 찾을 수 없습니다."));
        // 1단계, childrenId 으로 원아가 있는지 DB에 조회한다.

        ClassRoom classRoom = classRoomRepository.findById(classRoomId)
                .orElseThrow(() -> new IllegalArgumentException("반을 찾을 수 없습니다."));
        // 2단계, classRoomId 으로 반이 있는지 DB에 조회한다.

        if (!hasAvailableMaxChildren(classRoom)) {
            // 배정할려고 했는데 정원이 초과되었다면 ?!

            throw new IllegalStateException("해당 반의 정원이 초과되었습니다.");
        }

        children.setAssignedClass(classRoom);
        // 원아를 배정된 반에다가 넣어준다.

        classRoom.setCurrentStudents(classRoom.getCurrentStudents() + 1);
        // 그리고 넣어주고 반의 인원수를 증가 시킨다.

        childrenRepository.save(children);
        classRoomRepository.save(classRoom);
        // 배정 완료된 원아와 반을 DB에다가 저장한다.
    }

    public Long findNextUnassignedChild(Long parentId) {
        // 부모의 자녀들 중에서 아직 반 배정이 안 된 자녀들을 모두 찾음(한 부모의 여러 자녀가 있을 때)

        List<Children> unassignedChildren = childrenRepository.findByParent_ParentIdAndAssignedClassIsNull(parentId);

        return unassignedChildren.isEmpty() ? null : unassignedChildren.get(0).getChildrenId();
        // 삼항 연산자를 이용해서 미배정 자녀가 있으면? 자녀의 ID를 반환, 없으면? NULL 반환
    }

    private boolean hasAvailableMaxChildren(ClassRoom classRoom) {
        // 배정된 반에 원아가 등록이 되는지 확인 하는 메서드

        int currentCount = classRoom.getCurrentStudents() != null ?
                classRoom.getCurrentStudents() : 0;
        // 원아 수 확인하여 NULL이면 0으로 설정하고 NULL이 아니면 현재 값 사용

        return currentCount < classRoom.getMaxChildren();
    }

    private ClassRoomDTO convertToDTO(ClassRoom classRoom) {
        // 엔티티를 DTO로 변환

        return ClassRoomDTO.builder()
                .classRoomId(classRoom.getClassRoomId())
                .classRoomName(classRoom.getClassRoomName())
                .maxChildren(classRoom.getMaxChildren())
                .currentStudents(classRoom.getCurrentStudents())
                .employeeName(classRoom.getEmployeeName())
                .build();
    }

    public List<ClassRoomDTO> getAllClassRooms() {
        // 개설된 모든 반 목록을 조회하는 메서드

        return classRoomRepository.findAll().stream() .map(this::convertToDTO)
                .collect(Collectors.toList());
        // DB에 개설된 모든 반 정보를 가지고 와서 리스트로 만든다 !
    }
}
