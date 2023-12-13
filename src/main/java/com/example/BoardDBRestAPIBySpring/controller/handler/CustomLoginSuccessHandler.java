package com.example.BoardDBRestAPIBySpring.controller.handler;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import com.example.BoardDBRestAPIBySpring.config.jwt.TokenUtils;
import com.example.BoardDBRestAPIBySpring.domain.Token;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@Service
public class CustomLoginSuccessHandler implements  AuthenticationSuccessHandler{

    private final TokenUtils tokenUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증 성공 후 처리할 작업을 구현합니다.
        // 예를 들어, 세션 관리, 로그 기록, 사용자 정보 업데이트 등을 수행할 수 있습니다.

        // 인증 성공 후 리다이렉트할 URL을 설정합니다.

        PrincipalDetails principalDetails=(PrincipalDetails)authentication.getPrincipal();

//        Token token=tokenUtils.createToken(authentication).;
        String accessToken=tokenUtils.generateJwtToken(authentication);

        response.setHeader(JWTProperties.HEADER_STRING, accessToken);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("/login/successLogin");
    }
}
