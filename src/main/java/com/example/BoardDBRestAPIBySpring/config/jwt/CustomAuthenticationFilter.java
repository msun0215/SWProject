package com.example.BoardDBRestAPIBySpring.config.jwt;

import io.jsonwebtoken.IncorrectClaimException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // AccessToken 추출
        String accessToken = resolveToken(request);

        try {    // 정상 토큰인지 검사
            if (accessToken != null && jwtTokenProvider.validateAccessToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Save authentication in SecurityContextHolder");
                ;
            }
        } catch (IncorrectClaimException e) { // 잘못된 토큰일 경우
            SecurityContextHolder.clearContext();
            log.debug("Invalid JWT token");
            response.sendError(403);
        } catch (UsernameNotFoundException e) {   // 회원을 찾을 수 없는 경우
            SecurityContextHolder.clearContext();
            log.debug("Can't fint Member");
            response.sendError(403);
        }

        filterChain.doFilter(request, response);
    }

    // HTTP Request Header로부터 Token 추출
    public String resolveToken(HttpServletRequest httpServletRequest){
        String bearerToken=httpServletRequest.getHeader(JWTProperties.HEADER_STRING);
        if(bearerToken!=null&&bearerToken.startsWith(JWTProperties.TOKEN_PREFIX))
                return bearerToken.substring(7);

        return null;
    }
}