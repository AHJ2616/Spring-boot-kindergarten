package com.kinder.kindergarten.repository.children;

import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ChildrenRepository extends JpaRepository<Children, Long> {

    List<Children> findByParent_ParentIdAndAssignedClassIsNull(Long parentId);// parent_id와 assignedClass가 null인 원아 찾기

    List<Children> findByParent_ParentId(Long parentId); // 특정 부모의 모든 자녀 찾기

    long countByAssignedClass(ClassRoom classRoom); // 특정 반의 원아 수 카운트

    Page<Children> findByChildrenNameContaining(String keyword, Pageable pageable);// 검색할 때 원아의 이름으로 조회 메서드

    Page<Children> findByAssignedClass_ClassRoomNameContaining(String keyword, Pageable pageable);// 검색할 때 반 이름으로 조회 메서드

    Page<Children> findByParent_ParentNameContaining(String keyword, Pageable pageable);// 검색할 때 부모의 이름으로 조회 메서드
}
