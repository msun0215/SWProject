package com.example.BoardDBRestAPIBySpring.config.jwt;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetailsService;
import com.example.BoardDBRestAPIBySpring.domain.AuthDTO;
import com.example.BoardDBRestAPIBySpring.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

//https://velog.io/@u-nij
@Slf4j
@Component
@Transactional(readOnly = true)
public class JWTTokenProvider implements InitializingBean {

    private final PrincipalDetailsService principalDetailsService;
    private final RedisService redisService;

    private static final String AUTHROITIES_KEY="role";
    private static final String MEMBERID_KEY="memberID";
    private static final String url="http://localhost:8080";

    private final String secretKey;
    private static Key signingKey;

    private final Long accessTokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMiliseconds;


    public JWTTokenProvider(
            PrincipalDetailsService principalDetailsService,
            RedisService redisService,
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") Long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshTokenValidityInMiliseconds){
        this.principalDetailsService=principalDetailsService;
        this.redisService=redisService;
        this.secretKey=secretKey;
        // seconds->milliseconds
        this.accessTokenValidityInMilliseconds=accessTokenValidityInMilliseconds*1000;
        this.refreshTokenValidityInMiliseconds=refreshTokenValidityInMiliseconds*1000;
    }

    // Secret Key 설정
    public void afterPropertiesSet() throws Exception{
        byte[] secretKeyBytes= Decoders.BASE64.decode(secretKey);
        signingKey= Keys.hmacShaKeyFor(secretKeyBytes);
    }

    @Transactional
    public AuthDTO.TokenDto createToken(String memberID, String authorities){
        Long now=System.currentTimeMillis();

        String accessToken= Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now+accessTokenValidityInMilliseconds))
                .setSubject("access-token")
                .claim(url,true)
                .claim(MEMBERID_KEY, memberID)
                .claim(AUTHROITIES_KEY, authorities)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();


        String refreshToken= Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now+refreshTokenValidityInMiliseconds))
                .setSubject("refresh-token")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        return new AuthDTO.TokenDto(accessToken,refreshToken);
    }

    // Token으로부터 정보 추출(Token 검증)
    public Claims getClaims(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(signingKey)
                    .build().parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e) {  // Access Token
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String token){
        String memberID=getClaims(token).get(MEMBERID_KEY).toString();
        PrincipalDetails principalDetails= (PrincipalDetails) principalDetailsService.loadUserByUsername(memberID);
        return new UsernamePasswordAuthenticationToken(principalDetails,"",principalDetails.getAuthorities());
    }

    public long getTokenExpirationTime(String token){
        return getClaims(token).getExpiration().getTime();
    }

    // Token 검증
    public boolean validateRefreshToken(String refreshToken){
        try{
            if(redisService.getValues(refreshToken).equals("delete"))   return false;  //회원 탈퇴?

            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(refreshToken);
            return true;
        }catch(SignatureException e){
            log.error("Invalid JWT signature");
        }catch(MalformedJwtException e){
            log.error("Invalid JWT token");
        }catch(ExpiredJwtException e){
            log.error("Expired JWT token");
        }catch(UnsupportedJwtException e){
            log.error("Unsupported JWT token");
        }catch(IllegalArgumentException e){
            log.error("JWT claims string is empty");
        }catch(NullPointerException e){
            log.error("JWT Token is empty");
        }
        return false;
    }


    // Filter에서 사용
    public boolean validateAccessToken(String accessToken){
        try{
            if(redisService.getValues(accessToken)!=null    // NPE 방지
            &&redisService.getValues(accessToken).equals("logout")) // 로그아웃일 경우
                return false;

            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(accessToken);
            return true;
        }catch (ExpiredJwtException e){
            return true;
        }catch(Exception e){
            return false;
        }
    }

    // 재발급 검증 API에서 사용
    public boolean validateAccessTokenOnlyExpired(String accessToken){
        try{
            return getClaims(accessToken).getExpiration().before(new Date());
        }catch(ExpiredJwtException e){
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
