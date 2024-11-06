package com.kinder.kindergarten.service.children;

import com.kinder.kindergarten.DTO.children.ClassRoomDTO;
import com.kinder.kindergarten.entity.children.ClassRoom;
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

    public void classRoomRegister(ClassRoomDTO classRoomDTO) {
        // DTO를 엔티티로 변환
        ClassRoom classRoom = ClassRoom.builder()
                .classRoomName(classRoomDTO.getClassRoomName())
                .maxChildren(classRoomDTO.getMaxChildren())
                .classRoomDescription(classRoomDTO.getClassRoomDescription())
                .employeeName(classRoomDTO.getEmployeeName())
                .currentStudents(0)
                .build();

        log.info("반 등록: {}", classRoom);
        classRoomRepository.save(classRoom);
    }

    @Transactional(readOnly = true)
    public List<ClassRoomDTO> getAllClassRooms() {
        List<ClassRoom> classRooms = classRoomRepository.findAll();
        log.info("조회된 반 목록: {}", classRooms);

        List<ClassRoomDTO> dtoList = new ArrayList<>();

        for (ClassRoom classRoom : classRooms) {
            ClassRoomDTO dto = new ClassRoomDTO();
            dto.setClassRoomId(classRoom.getClassRoomId());
            dto.setClassRoomName(classRoom.getClassRoomName());
            dto.setMaxChildren(classRoom.getMaxChildren());
            dto.setClassRoomDescription(classRoom.getClassRoomDescription());
            dto.setEmployeeName(classRoom.getEmployeeName());
            dto.setCurrentStudents(classRoom.getCurrentStudents());

            log.info("변환된 DTO: {}", dto);
            dtoList.add(dto);
        }

        return dtoList;
    }



}
