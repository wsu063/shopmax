package com.shopmax.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

//로그인한 사용자의 id를 등록자와 수정자로 수정
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        //로그인한 사용자의 정보를 가지고 온다
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String userId = "";

        if(authentication != null) {
            userId = authentication.getName(); // 로그인한 사용자의 id(여기는 email)을 가지고 온다.
        }

        return Optional.of(userId); // 해당 userId를 등록자와 수정자로 지정
    }
}
