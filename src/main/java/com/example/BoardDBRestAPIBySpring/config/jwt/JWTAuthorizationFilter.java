package com.example.BoardDBRestAPIBySpring.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

// Security가 가지고 있는 filter들 중 BasicAuthenticationFilter라는 것이 있음
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 filter를 무조건 타게 되어있음.
// 만약에 권한이 인증이 필요한 주소가 아니라면 이 filter를 타지 않는다.
// 인가

@Log4j2
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final MemberRepository memberRepository;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository){
        super(authenticationManager);
        System.out.println("인증이나 권한이 필요한 주소가 요청됨");
        this.memberRepository=memberRepository;
    }


    // 인증이나 권한이 필요한 주소 요청이 있을 때 해당 필터를 타게 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //String header=request.getHeader("Authorization");
        System.out.println("request : "+request);
        System.out.println("Authorization : "+request.getHeader(JWTProperties.HEADER_STRING));
        String header=request.getHeader(JWTProperties.HEADER_STRING);
        System.out.println("JWTAuthorizationFilter에서의 JWTHeader : "+header);  // JWT Token

//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println("Header: " + headerName + ", Value: " + request.getHeader(headerName));
//        }


        // header가 있는지(유효한지) 확인
        if(header==null||!header.startsWith(JWTProperties.TOKEN_PREFIX)){
            System.out.println("Not Allowed User");
            chain.doFilter(request,response);
            return;
        }

        // JWT Token을 검증해서 정상적인 사용자인지 확인
        String token = request.getHeader(JWTProperties.HEADER_STRING).replace(JWTProperties.TOKEN_PREFIX, "");
//        String memberID = JWT.require(Algorithm.HMAC512(JWTProperties.SECRET)).build().verify(token).getClaim("memberID").asString();  // verify()를 통해서 서명

        boolean validToken = TokenUtils.isValidToken(token);
        System.out.println("validToken = " + validToken);
        String memberID = JWT.require(Algorithm.HMAC512(JWTProperties.SECRET)).build().verify(token).getClaim("id").asString();  // verify()를 통해서 서명

        System.out.println("token : "+token);
        System.out.println("memberID : "+memberID);
        // 서명이 정상적으로 동작했을 경우
        if(memberID!=null){
            Member memberEntity = memberRepository.findByMemberID(memberID);
            System.out.println("UserEntity 정상 : "  +memberEntity);
            PrincipalDetails principalDetails = new PrincipalDetails(memberEntity);
            System.out.println("MemberName : "+memberEntity.getMemberName());

            // JWT Token 서명을 통해서 서명이 정상적이면 Authentication 객체를 만들어준다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            System.out.println("authentication"+authentication);
            // 강제로 Security의 Session에 접근하여서 Authentication 객체를 저장시킨다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Successfully Saved Authentication" + authentication);

        }
        // super.doFilterInternal(request, response, chain);
        chain.doFilter(request,response);
    }
}
