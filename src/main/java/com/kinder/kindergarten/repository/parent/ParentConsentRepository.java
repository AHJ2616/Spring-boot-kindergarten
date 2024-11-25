package com.kinder.kindergarten.repository.parent;

import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.parent.ParentConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentConsentRepository extends JpaRepository<ParentConsent, Long> {

    Optional<ParentConsent> findByParent_ParentId(Long parentId);
    // 학부모 ID로 동의서 찾기

    boolean existsByParentParentId(Long parentId);
    // 동의서가 존재하는지 확인

    // Member 이메일로 각 동의 상태 조회
    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email AND pc.termsConsent = true")
    boolean existsByMemberEmailAndTermsConsentIsTrue(@Param("email") String email);
    // Member 이메일로 ERP 시스템 및 커뮤니티 서비스 이용 약관의 동의 상태를 조회

    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email AND pc.communityConsent = true")
    boolean existsByMemberEmailAndCommunityConsentIsTrue(@Param("email") String email);
    // Member 이메일로 학부모 커뮤니티 활동 동의서의 동의 상태를 조회

    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email AND pc.privateConsent = true")
    boolean existsByMemberEmailAndPrivateConsentIsTrue(@Param("email") String email);
    // Member 이메일로 학부모 개인정보 및 제3자 제공 동의서의 동의 상태를 조회

    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email AND pc.photoConsent = true")
    boolean existsByMemberEmailAndPhotoConsentIsTrue(@Param("email") String email);
    // Member 이메일로 사진 및 영상 촬영, 활용 동의서의 동의 상태를 조회

    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email AND pc.medicalInfoConsent = true")
    boolean existsByMemberEmailAndMedicalInfoConsentIsTrue(@Param("email") String email);
    // Member 이메일로 의료정보 동의서의 동의 상태를 조회

    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email AND pc.emergencyInfoConsent = true")
    boolean existsByMemberEmailAndEmergencyInfoConsentIsTrue(@Param("email") String email);
    // Member 이메일로 비상연락망 동의서의 동의 상태를 조회


    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
            "FROM ParentConsent pc " +
            "JOIN pc.parent p " +
            "WHERE p.memberEmail = :email")
    boolean existsByMemberEmail(@Param("email") String email);// Member 이메일로 동의서 존재 여부 확인
}
