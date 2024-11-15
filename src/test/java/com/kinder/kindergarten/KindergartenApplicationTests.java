package com.kinder.kindergarten;

import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KindergartenApplicationTests {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    void saveMemberTest() {
        // given
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail("admin@test.com");
        memberDTO.setPassword("123123123");
        memberDTO.setName("테스트");
        memberDTO.setPhone("010-1234-5678");
        memberDTO.setAddress("수원");
        memberDTO.setRole("ROLE_ADMIN");

        // when
        memberService.saveMember(memberDTO);
    }

    @Test
    void contextLoads() {
    }

}
