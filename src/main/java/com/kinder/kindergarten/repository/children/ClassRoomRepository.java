package com.kinder.kindergarten.repository.children;

import com.kinder.kindergarten.entity.children.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    // ClassRoom의 레포지토리

    Optional<ClassRoom> findByClassRoomName(String className);
    // 반 이름으로 반 정보를 조회하는 메서드
    boolean existsByClassRoomName(String className);
    // 반 이름의 중복 여부를 확인 하는 메서드
}
