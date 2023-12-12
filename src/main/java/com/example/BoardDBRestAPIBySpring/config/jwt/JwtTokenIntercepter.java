package com.example.BoardDBRestAPIBySpring.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
@Service
@Component
public class JwtTokenIntercepter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String header=request.getHeader(JWTProperties.HEADER_STRING);

        System.out.println("Interceptor에서 JWTToken 호출 : "+header);

        if(header!=null&&TokenUtils.isValidToken(header)){
            return true;
        }
        response.setStatus(401);
        response.setHeader(JWTProperties.HEADER_STRING, header);
        response.setHeader("msg", "Check the Token");
        response.sendRedirect("/error/unauthorized");
        return false;
    }
}
