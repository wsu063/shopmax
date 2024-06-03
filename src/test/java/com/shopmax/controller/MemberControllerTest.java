package com.shopmax.controller;

import com.shopmax.dto.MemberFormDto;
import com.shopmax.entity.Member;
import com.shopmax.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 트랜잭션 처리: 중간에 에러 발생시 rollback을 시켜준다.
@AutoConfigureMockMvc // MockMvc 테스트를 위해 어노테이션 선언
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc; // 모형 객체 => 웹 브라우저에서 request하는것 처럼 테스트 할 수 있는 객체

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception {
        String email = "test@gmail.com";
        String password = "1234";
        createMember(email, password);

        mockMvc.perform(SecurityMockMvcRequestBuilders
                .formLogin().userParameter("email")
                .loginProcessingUrl("/members/login") // 로그인 처리할 경로.
                .user(email).password(password)) // 아이디와 비밀번호로 로그인처리
                .andExpect(SecurityMockMvcResultMatchers.authenticated()); // 로그인 성공하면 테스트코드 통과
    }

    @Test
    @DisplayName("로그인 실패테스트")
    public void loginFailTest() throws Exception {
        String email = "test@gmail.com";
        String password = "1234";
        createMember(email, password);

        mockMvc.perform(SecurityMockMvcRequestBuilders
                        .formLogin().userParameter("email")
                        .loginProcessingUrl("/members/login") // 로그인 처리할 경로.
                        .user(email).password("12345")) // 아이디와 비밀번호로 로그인처리
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated()); // 로그인 실패하면 테스트코드 통과
    }

}
