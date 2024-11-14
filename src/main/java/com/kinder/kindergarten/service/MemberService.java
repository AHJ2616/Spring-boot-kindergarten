package com.kinder.kindergarten.service;

import com.kinder.kindergarten.entity.FcmTokenEntity;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.repository.FcmTokenRepository;
import com.kinder.kindergarten.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

  private final MemberRepository memberRepository;

  private final FcmTokenRepository fcmTokenRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email);
            
    return User.builder()
        .username(member.getEmail())
        .password(member.getPassword())
        .roles(member.getRole().toString())
        .build();
  }

  public void addFCMToken(String email, String fcmToken){
    Optional<Member> member = Optional.ofNullable(memberRepository.findByEmail(email));
    FcmTokenEntity token = new FcmTokenEntity(member.orElseThrow(), fcmToken);
    fcmTokenRepository.save(token);
  }
}
