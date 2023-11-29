package com.example.BoardDBRestAPIBySpring.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtTokenIntercepter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String header=request.getHeader(JWTProperties.HEADER_STRING);

        if(header!=null){
            final String token=TokenUtils.getTokenFromHeader(header);
            if(TokenUtils.isValidToken(token))  return true;

        }
        response.sendRedirect("/error/unauthorized");
        return false;
    }
}