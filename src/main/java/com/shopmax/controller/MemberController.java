package com.shopmax.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {


    //문의하기
    @GetMapping(value = "/members/qa") //localhost/members/qa
    public String qa() {
        return "member/qa";
    }
    //로그인 화면
    @GetMapping(value = "/members/login") // localhost/members/login
    public String loginMember() {
        return "member/memberLoginForm";
    }
    //회원가입 화면
    @GetMapping(value = "/members/new") // localhost/members/new
    public String memberForm() {
        return "member/memberForm";
    }

//    //로그아웃 화면
//    @GetMapping(value = "/members/logout") // localhost/members/logout
//    public String logoutMember() {
//        return "member/memberLogoutForm";
//    }
}
