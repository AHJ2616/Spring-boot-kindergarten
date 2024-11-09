package com.kinder.kindergarten.repository.children;

import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ClassRoom;
import com.kinder.kindergarten.entity.parent.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ChildrenRepository extends JpaRepository<Children, Long> {
    // parent_id와 assignedClass가 null인 원아 찾기
    List<Children> findByParent_ParentIdAndAssignedClassIsNull(Long parentId);

    // 특정 부모의 모든 자녀 찾기
    List<Children> findByParent_ParentId(Long parentId);

    // 특정 반의 원아 수 카운트
    long countByAssignedClass(ClassRoom classRoom);
}
