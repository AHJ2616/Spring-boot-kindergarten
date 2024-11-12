package com.kinder.kindergarten.service;

import com.kinder.kindergarten.entity.FcmTokenEntity;
import com.kinder.kindergarten.entity.MemberEntity;
import com.kinder.kindergarten.repository.FcmTokenRepository;
import com.kinder.kindergarten.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  private final FcmTokenRepository fcmTokenRepository;

  public void addFCMToken(String email, String fcmToken){
    Optional<MemberEntity> member = Optional.ofNullable(memberRepository.findbymember_email(email));
    FcmTokenEntity token = new FcmTokenEntity(member.orElseThrow(), fcmToken);
    fcmTokenRepository.save(token);
  }
}
