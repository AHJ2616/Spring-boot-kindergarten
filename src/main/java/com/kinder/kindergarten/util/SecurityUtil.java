package com.kinder.kindergarten.util;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.entity.Employee.Employee;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public boolean hasPermission(Long employeeId, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return principalDetails.getEmployee().getId().equals(employeeId) ||
                principalDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }
        return ((PrincipalDetails) authentication.getPrincipal()).getEmployee();
    }
}