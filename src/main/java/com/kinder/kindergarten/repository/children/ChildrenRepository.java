package com.kinder.kindergarten.repository.children;

import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.parent.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildrenRepository extends JpaRepository<Children, Long> {

    // Children Repository

    Optional<Children> findByParent(Parent parent);
    // 특정 학부모로부터 원아 검색 조회

}
