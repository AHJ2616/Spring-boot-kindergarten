package com.kinder.kindergarten.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    // Parent(부모) 레포지토리

    //  Repository에서는 Optional<>을 사용하는 것이 일반적이기 때문에 jpa 메서드는 기본적으로 Optional을 반환 한다.
    // Repository 작성할 때 find하면 여러 목록이 나오는데 이때, 엔티티의 필드명과 메서드이름을 일치하도록 작성해야 한다!
    @Query("SELECT p FROM Parent p WHERE p.parent_email = :email")
    Optional<Parent> findByEmail(@Param("email") String email);
    // 회원 가입 시 중복된 회원이 있는지 검사하기 위해 이메일로 회원 검사하는 쿼리 메서드(책에 나온대로 하니 오류발생으로 @Query 이용)
    // @Query 이용할 때는 @Param을 이용하여 데이터 지정

    @Query("SELECT p FROM Parent p WHERE p.parent_name = :parentName AND p.parent_birthDate =:parentBirthdate AND p.parent_phone = :parentPhone")
    Optional<Parent> findByParentNameAndParentBirthdateAndParentPhone(
            // 회원 가입 시 입력했던 성함, 생년월일, 핸드폰번호를 찾아서 아이디 찾기 기능 이용

            @Param("parentName") String parentName,
            @Param("parentBirthdate") String parentBirthdate,
            @Param("parentPhone") String parentPhone);

    /* 여기서 알게 된 점 ->JPA는 메서드명 기반 쿼리를 생성할 때 언더스코어(_)가 없는 필드명을 기본으로 인식한다.
         Parent 엔티티의 필드명을 언더스코어 없이 일관되게 수정하는 것 좋다.

        즉, 엔티티와 필드명을 JPA 메서드명과 맞춰야 하는데 언더스코어(_)가 있으면 인식을 못하므로 되도록이면
        엔티티 필드에 언더스코어 쓰지 말기(바꾸게 되면 DB까지 연관되어 있어 해당 필드에 데이터가 연관 되어 있으면 불일치로 오류가 난다.
     */

}
