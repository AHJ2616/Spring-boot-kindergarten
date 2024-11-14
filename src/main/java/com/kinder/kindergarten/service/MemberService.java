package com.kinder.kindergarten.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.entity.FcmTokenEntity;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.exception.OutOfStockException;
import com.kinder.kindergarten.repository.FcmTokenRepository;
import com.kinder.kindergarten.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final FcmTokenRepository fcmTokenRepository;

    public Member saveEmployee(Member member){
        // 직원등록 메서드
        validateDuplicateEmployee(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateEmployee(Member member){
        // 직원등록 메서드
        Member em = memberRepository.findByEmail(member.getEmail());
        if(em != null){
            throw new OutOfStockException("이미 등록되어있는 직원입니다.");
        }
    }

    public void addFCMToken(String email, String fcmToken){
        Optional<Member> member = Optional.ofNullable(memberRepository.findByEmail(email));
        FcmTokenEntity token = new FcmTokenEntity(member.orElseThrow(), fcmToken);
        fcmTokenRepository.save(token);
    }

    private MemberDTO covertToMemberDTO(Employee employee){

        // 엔티티로 넘어온 값을 DTO 타입으로 변형
        MemberDTO memberdto = new MemberDTO();
        memberdto.setName(employee.getName());
        memberdto.setEmail(employee.getEmail());
        memberdto.setAddress(employee.getAddress());
        memberdto.setPassword(employee.getPassword());
        memberdto.setPhone(employee.getPhone());
        return memberdto;
    }

}
