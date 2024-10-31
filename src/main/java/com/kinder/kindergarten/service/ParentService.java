package com.kinder.kindergarten.service;

import com.kinder.kindergarten.DTO.EmailFindDTO;
import com.kinder.kindergarten.DTO.ParentUpdateFormDTO;
import com.kinder.kindergarten.entity.Parent;
import com.kinder.kindergarten.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParentService implements UserDetailsService {

    private final ParentRepository parentRepository;

    private final PasswordEncoder passwordEncoder;



    public  Parent saveParent(Parent parent) {

        validateDuplicateParent(parent);

        return parentRepository.save(parent);
    }// 학부모 회원가입 정보 저장 메서드

    private void validateDuplicateParent(Parent parent) {

        if (parentRepository.findByEmail(parent.getParent_email()).isPresent()) {

            throw new IllegalStateException("이미 가입된 계정 입니다.");
        }

    }// 이미 가입된 계정이라면 예외를 발생 시킨다.(학부모 중복 계정 검사 메서드)


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Parent parent = parentRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(email));

        return User.builder()
                .username(parent.getParent_email())
                .password(parent.getParent_pw())
                .roles(parent.getChildren_role().toString())
                .build();
    }// 학부모 데이터가 null이면 입센션 발생시키고 아니면 이메일과 비밀번호, 권한으로 데이터 생성

    public Parent parentEmailGet(String email) {
        // 학부모의 이메일 정보를 가져와서 정보를 조회하는 메서드

        return parentRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("해당되는 계정의 정보가 없습니다." + email));
    }

    @Transactional
    public Parent updateParent(String email, Parent updatedParentData) {
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 계정을 찾을 수 없습니다."));

        // 변경 감지(Dirty Checking)를 사용한 업데이트
        parent.setParent_name(updatedParentData.getParent_name());
        parent.setParent_phone(updatedParentData.getParent_phone());
        parent.setParent_address(updatedParentData.getParent_address());

        return parent; // 트랜잭션 종료 시 자동으로 업데이트됨
    }

    public void deleteParent(String email) {
        // 학부모 계정 탈퇴 하는 메서드 (D)

        Parent parent = parentRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("해당 계정을 찾을 수 없습니다." + email));

        parentRepository.delete(parent);

    }
    
    public String findParentEmail(EmailFindDTO emailFindDTO) {
        // 이메일 찾기 기능 메서드 -> 회원 가입할 때 받았던 성함, 생년월일, 핸드폰 번호를 이용하여 서비스 가동
        
       return parentRepository.findByParentNameAndParentBirthdateAndParentPhone(
           
               emailFindDTO.getParent_name(),
               emailFindDTO.getParent_birthDate(),
               emailFindDTO.getParent_phone()).map(Parent::getParent_email) // Parent 객체에서 이메일 반환
               .orElse(null);// 조회된 값이 없으면 null 반환
                
    }


    @Transactional
    public String passwordChange(String email) {
        // 비밀번호 찾기 기능 서비스 메서드

        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 등록된 계정이 없습니다."));

        // 임시 비밀번호 생성 (8자리 랜덤 문자열)
        String tempPassword = RandomStringUtils.randomAlphanumeric(8);

        // 임시 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(tempPassword);
        parent.setParent_pw(encodedPassword);

        // 변경된 비밀번호 저장
        parentRepository.save(parent);

        return tempPassword;
    }
}
