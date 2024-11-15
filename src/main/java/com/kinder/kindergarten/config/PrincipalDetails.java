package com.kinder.kindergarten.config;


import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

  private Member member;
  private Employee employee;

  public PrincipalDetails(Member member, Employee employee){
    this.member = member;
    this.employee = employee;
  }


  public Member getMember() {
    return this.member;
  }

  public Employee getEmployee() {
    return this.employee;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
    authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole()));
    return authorities;
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
