package com.kinder.kindergarten.util;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

  public boolean hasPermission(String email, Authentication authentication) {
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    return principalDetails.getMember().getEmail().equals(email) ||
            principalDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }

  public Member getCurrentEmployee() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
    }
    return ((PrincipalDetails) authentication.getPrincipal()).getMember();
  }
}