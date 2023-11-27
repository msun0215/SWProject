package com.example.BoardDBRestAPIBySpring.config.jwt;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {
    //private final UserDetailsService userDetailsService;
    @Autowired
    private PrincipalDetailsService principalDetailsService;

    @Autowired
    private final BCryptPasswordEncoder encodePWD;

    // 인증 구현
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        System.out.println("Provider 접근!");
        // 전달 받은 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        System.out.println("Provider에서의 token : "+token);
        // JWTAuthenticationFilter에서 생성된 Token으로부터 ID와 PW 추출
        String memberID = token.getName();
        String memberPW = token.getCredentials().toString();
        System.out.println("Provider에서의 memberID : "+memberID);
        System.out.println("Provider에서의 memberPW : "+memberPW);

        // 해당 회원에 대해 DB 조회
        PrincipalDetails principalDetails= (PrincipalDetails)  principalDetailsService.loadUserByUsername(memberID);

        // 비밀번호 확인
        if(!encodePWD.matches(memberPW, principalDetails.getPassword()))
            throw new BadCredentialsException(principalDetails.getUsername() + "Invalid password");

        // 인증 성공 시 UsernamePasswordAuthenticationToken 반환
        System.out.println("Token 반환 성공! "+new UsernamePasswordAuthenticationToken(principalDetails,"",principalDetails.getAuthorities()));
        return new UsernamePasswordAuthenticationToken(principalDetails,principalDetails.getPassword(),principalDetails.getAuthorities());
    }

    // provider의 동작 여부를 설정함
    @Override
    public boolean supports(Class<?> authentication){
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
