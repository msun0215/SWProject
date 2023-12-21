package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTTokenProvider;
import com.example.BoardDBRestAPIBySpring.controller.handler.CustomAuthFailureHandler;
import com.example.BoardDBRestAPIBySpring.domain.AuthDTO;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*
 * 요청 -> AT 검사 -> AT 유효 -> 요청 실행
 * 요청 -> AT 검사 -> AT 기간만 만료 -> AT, RT로 재발급 요청 -> RT 유효 -> 재발급
 * 요청 -> AT 검사 -> AT 기간만 만료 -> AT, RT로 재발급 요청 -> RT 유효X -> 재로그인
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final MemberRepository memberRepository;
    private final String SERVER="Server";
    // RefreshToken을 생성한 후 Redis에 {key:RT({발급자}):{memberID}, value:{RT}} 형식으로 저장
    // Oauth2.0 OPEN API 적용 시 사용함

    // 로그인 : 인증 정보 저장 및 Bearer 토큰 발급
    @Transactional
    public AuthDTO.TokenDto login(AuthDTO.LoginDto loginDto){
        System.out.println("====================================");
        System.out.println("Access To Login");
        System.out.println("====================================");


        if(memberRepository.findByMemberID(loginDto.getMemberID())==null){
            log.debug("memberID doesn't exists");
            return null;
        }else {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getMemberID(), loginDto.getMemberPW());

            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return generateToken(SERVER, authentication.getName(), getAuthorities(authentication));
        }
    }

    // AccessToken이 만료일자만 초과한 유효한 Token인지 검사
    public boolean validate(String requestAccessTokenInHeader){
        System.out.println("====================================");
        System.out.println("Access To Validate AccessToken");
        System.out.println("====================================");

        String requestAccessToken=resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true->재발급
    }

    // Token 재발급 : validate 메서드가 true를 반환할 때만 사용함 -> AccessToken, RefreshToken 재발급
    @Transactional
    public AuthDTO.TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken){
        System.out.println("====================================");
        System.out.println("Access To reissue AccessToken");
        System.out.println("====================================");

        String requestAccessToken=resolveToken(requestAccessTokenInHeader);
        Authentication authentication= jwtTokenProvider.getAuthentication(requestAccessToken);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT("+SERVER+")"+principal);
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
        redisService.deleteValues("RT("+SERVER+")"+principal);     // 기존 RefreshToken 삭제
        AuthDTO.TokenDto tokenDto=jwtTokenProvider.createToken(principal,authorities);
        saveRefreshToken(SERVER, principal, tokenDto.getRefreshToken());
        return tokenDto;
    }

    // Token 발급
    @Transactional
    public AuthDTO.TokenDto generateToken(String provider, String memberID, String authorities){
        System.out.println("====================================");
        System.out.println("Access To Generate Token");
        System.out.println("====================================");

        // RefreshToken이 이미 있는 경우
        if(redisService.getValues("RT("+provider+"):"+memberID)!=null) {
            System.out.println("RefreshToken is already exists. Delete RefreshToken");
            redisService.deleteValues("RT(" + provider + ")" + memberID);    // 삭제
        }

        // AccessToken, RefreshToken 생성 및 Redis에 RefreshToken 저장
        AuthDTO.TokenDto tokenDto=jwtTokenProvider.createToken(memberID, authorities);
        System.out.println("Get TokenDTO");
        System.out.println("AccessToken : "+tokenDto.getAccessToken());
        System.out.println("RefreshToken : "+tokenDto.getRefreshToken());
        saveRefreshToken(provider, memberID, tokenDto.getRefreshToken());
        return tokenDto;
    }


    // RfreshToken을 Redis에 저장
    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken){
        System.out.println("====================================");
        System.out.println("Access To SaveRefreshToken : "+refreshToken);;
        System.out.println("====================================");

        redisService.setValuesWithTimeout("RT("+provider+")"+principal,  //key
                refreshToken,    // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication){
        String getAuth=authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","));

        System.out.println("getAuthorities : "+getAuth);
        return getAuth;

    }

    // AccessToken으로부터 principal 추출
    public String getPrincipal(String requestAccessToken){
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    public Authentication getAuthentication(String requestAccessToken){
        String token = resolveToken(requestAccessToken);
        return jwtTokenProvider.getAuthentication(token);
    }

    // "Bearer {AT}"에서 "{AT}" 추출
    public String resolveToken(String requestAccessTokenInHeader){
        if(requestAccessTokenInHeader!=null&&requestAccessTokenInHeader.startsWith(JWTProperties.TOKEN_PREFIX))
            return requestAccessTokenInHeader.substring(7);

        return null;
    }


    // logout
    @Transactional
    public void logout(String requestAccessTokenInHeader){
        String requestAccessToken=resolveToken(requestAccessTokenInHeader);
        String principal=getPrincipal(requestAccessToken);

        // Redis에 저장되어 있는 RefreshToken 삭제
        String refreshTokenInRedis=redisService.getValues("RT("+SERVER+")"+principal);
        if(refreshTokenInRedis!=null)
            redisService.deleteValues("RT("+SERVER+")"+principal);

        // Redis에 logout 처리한 AccessToken 저장
        long expiration=jwtTokenProvider.getTokenExpirationTime(requestAccessToken)-new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessToken,"logout",expiration);
    }
}
