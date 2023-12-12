package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.config.jwt.JWTTokenProvider;
import com.example.BoardDBRestAPIBySpring.domain.AuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;

    private final String SERVER="Server";

    // 로그인 : 인증 정보 저장 및 Bearer 토큰 발급
    @Transactional
    public AuthDTO.TokenDto login(AuthDTO.LoginDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(loginDto.getMemberID(), loginDto.getMemberPW());

        Authentication authentication=authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return generateToken(SERVER, authentication.getName(), getAuthorities(authentication));
    }

    // AccessToken이 만료일자만 초과한 유효한 Token인지 검사
    public boolean validate(String requestAccessTokenInHeader){
        String requestAccessToken=resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true->재발급
    }

    // Token 재발급 : validate 메서드가 true를 반환할 때만 사용함 -> AccessToken, RefreshToken 재발급
    @Transactional
    public AuthDTO.TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken){
        String requestAccessToken=resolveToken(requestAccessTokenInHeader);
        Authentication authentication= jwtTokenProvider.getAuthentication(requestAccessToken);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT("+SERVER+")"+principla);
        if(refreshTokenInRedis==null)       // Redis에 저장되어있는 RefreshToken이 없을 경우
            return null;                    // 재로그인 요청

        // 요청된 RefreshToken의 유효성 검사 & Redis에 저장되어있는 RefreshToken과 같은지 비교
        if(!jwtTokenProvider.validateRefreshToken(requestRefreshToken)||!refreshTokenInRedis.equals(requestRefreshToken)){
            redisService.deleteValues("RT("+SERVER+"):"+principal); // 탈취 가능성-> 삭제
            return null;    // 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities=getAuthorities(authentication);

        // Token 재발급 및 Redis 업데이트
        redisService.deleteValues("RT("+SERVER+"):"+principal);     // 기존 RefreshToken 삭제
        AuthDTO.TokenDto tokenDto=jwtTokenProvider.createToken(principal,authorities);
        saveRefreshToken(SERVER, principal, tokenDto.getRefreshToken());
        return tokenDto;
    }

    https://surf-kookaburra-9e3.notion.site/090e456609b0493793f0d0d94e8f91fa
    // Token 발급
    @Transactional
    public
}
