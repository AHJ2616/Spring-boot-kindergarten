package com.kinder.kindergarten.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmTokenEntity {

  @Id
  @Column(name="fcm_id")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name= "member_email")
  private MemberEntity member;

  private String token;

  public FcmTokenEntity(MemberEntity member,String token){
    this.id = UlidCreator.getUlid().toString();
    this.member = member;
    this.token = token;
  }
}
