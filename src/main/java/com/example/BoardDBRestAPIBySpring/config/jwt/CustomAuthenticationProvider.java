
package com.example.BoardDBRestAPIBySpring.config.jwt;


import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetailsService;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
//@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

//    @Autowired
//    private PrincipalDetailsService principalDetailsService;

    @Autowired
    private BCryptPasswordEncoder encodePWD;

    @Autowired
    private MemberRepository memberRepository;

    // 인증 구현
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        //Class<? extends Authentication> toTest=authentication.getClass();
        System.out.println("Provider 접근!");
        // 전달 받은 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        System.out.println("Provider에서의 token : "+token);
        // JWTAuthenticationFilter에서 생성된 Token으로부터 ID와 PW 추출
        String memberID = token.getName();
        String memberPW = token.getCredentials().toString();
        System.out.println("Provider에서의 memberID : "+memberID);
        System.out.println("Provider에서의 memberPW : "+memberPW);


        PrincipalDetailsService principalDetailsService=new PrincipalDetailsService(memberRepository);

        // 해당 회원에 대해 DB 조회
        //PrincipalDetails principalDetails= (PrincipalDetails)  principalDetailsService.loadUserByUsername(memberID);
        PrincipalDetails principalDetails=(PrincipalDetails) principalDetailsService.loadUserByUsername(memberID);
        System.out.println("CustomAuthenticationProvider에서 받은 principalDetails : "+principalDetails);
        System.out.println("CustomAuthenticationProvider에서 받은 principalDetails의 memberPW : "+principalDetails.getPassword());
        // 비밀번호 확인
        if(!encodePWD.matches(memberPW, principalDetails.getPassword())){
            System.out.println("Doesn't Match");
            throw new BadCredentialsException(principalDetails.getUsername() + "Invalid password");
        }else{
            // 인증 성공 시 UsernamePasswordAuthenticationToken 반환
            System.out.println("Token 반환 성공! ");
            System.out.println("new : "+new UsernamePasswordAuthenticationToken(principalDetails,"",principalDetails.getAuthorities()));
            return new UsernamePasswordAuthenticationToken(principalDetails,principalDetails.getPassword(),principalDetails.getAuthorities());
        }
    }

    // provider의 동작 여부를 설정함
    @Override
    public boolean supports(Class<?> authentication){
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
