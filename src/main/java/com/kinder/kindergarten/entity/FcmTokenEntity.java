package com.kinder.kindergarten.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fcm_token_entity")
public class FcmTokenEntity extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(nullable = false)
  private String token;

  public FcmTokenEntity(Member member, String token){
    this.member = member;
    this.token = token;
  }
}
