package com.kinder.kindergarten.repository.parent;

import com.kinder.kindergarten.entity.parent.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    // Parent Repository(JpaRepository에서 기본적인 CRUD 메서드 제공한다.)

    /*  Repository에서는 Optional<>을 사용하는 것이 일반적이기 때문에 jpa 메서드는 기본적으로 Optional을 반환 한다.
     Repository 작성할 때 find하면 여러 목록이 나오는데 이때, 엔티티의 필드명과 메서드이름을 일치하도록 작성해야 한다!

     여기서 알게 된 점 ->JPA는 메서드명 기반 쿼리를 생성할 때 언더스코어(_)가 없는 필드명을 기본으로 인식한다.
        Parent 엔티티의 필드명을 언더스코어 없이 일관되게 수정하는 것 좋다.

       즉, 엔티티와 필드명을 JPA 메서드명과 맞춰야 하는데 언더스코어(_)가 있으면 인식을 못하므로 되도록이면
       엔티티 필드에 언더스코어 쓰지 말기(바꾸게 되면 DB까지 연관되어 있어 해당 필드에 데이터가 연관 되어 있으면 불일치로 오류가 난다.
    */

    Optional<Parent> findByParentEmail(@Param("email") String email);
    // 회원 가입 시 중복된 회원이 있는지 검사하기 위해 이메일로 회원 검사하는 메서드

    Page<Parent> findByParentNameContaining(String keyword, Pageable pageable);
    // 학부모 성함으로 검색하는 메서드

    boolean existsByParentEmail(String parentEmail);
    // 이메일 존재 여부 확인하는 메서드
}
