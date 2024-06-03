package com.shopmax.controller;

import com.shopmax.dto.MemberFormDto;
import com.shopmax.entity.Member;
import com.shopmax.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor // 어노테이션을 사용한 생성자 주입 방법. 직접하려면 복잡해서 이걸 이용한다.
public class MemberController {

    private final PasswordEncoder passwordEncoder; // @RequiredArgs와 final로 필드선언하면 @Autowired된다

    private final MemberService memberService;

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
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto()); // 빈 객체 넘기기
        return "member/memberForm";
    }
    
    //회원가입 처리
    @PostMapping(value = "/members/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                             Model model) {
        // 유효성 체크시 @Valid를 붙인다.
        // BindingResult: 유효성 검증 후의 결과가 들어있다.
        
        // 유효성 검증 에러 발생시 회원가입 페이지로 이동
        if (bindingResult.hasErrors()) return "member/memberForm";
        try {
            //memberFormDto -> member Entity입력용
            Member member = Member.createMember(memberFormDto, passwordEncoder );
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            //회원가입이 이미 되어있다면
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/"; // 회원가입 완료 후 메인페이지로 이동
    }

    @GetMapping(value = "members/login/error")
    public String loginError(Model model) {
        //유효성 체크를 위해서 memberFormDto 객체를 매핑하기 위해 전달
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");

        return "member/memberLoginForm"; // 로그인 페이지로 그대로 이동
    }
    
    
}
