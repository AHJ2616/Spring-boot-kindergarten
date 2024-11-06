package com.kinder.kindergarten.entity.children;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass   // 직접적으로 테이블을 생성하지 않는 Entity
@EntityListeners(value = {AuditingEntityListener.class})    // 등록, 수정을 감시한다.
@Getter
public class ChildrenBaseEntity {

    // 학부모와 원아 엔티티에 대한 공통 엔티티(학부모는 아직 별개 구분)

    @CreatedDate    // 생성시간
    @Column(name = "createdDate", updatable = false)    // 한번 등록하면 수정할수 없게 한다.
    private LocalDateTime createdDate ; // 등록일 담당

    @LastModifiedDate
    @Column(name = "updatedDate")
    private LocalDateTime updatedDate ; // 수정일 담당

}
