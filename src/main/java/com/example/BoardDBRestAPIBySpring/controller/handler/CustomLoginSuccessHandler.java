package com.example.BoardDBRestAPIBySpring.controller.handler;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import com.example.BoardDBRestAPIBySpring.config.jwt.TokenUtils;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Log4j2
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        final Member member=((PrincipalDetails)authentication.getPrincipal()).getMember();
        final String token = TokenUtils.generateJwtToken(member);
//        response.addHeader(JWTProperties.HEADER_STRING, JWTProperties.TOKEN_PREFIX+token);

        ObjectMapper objectMapper = new ObjectMapper();

        ResponseData responseData = new ResponseData(JWTProperties.TOKEN_PREFIX + token);
        String json = objectMapper.writeValueAsString(responseData);
        response.getWriter().write(json);
    }


    private record ResponseData(String token) { }
}
