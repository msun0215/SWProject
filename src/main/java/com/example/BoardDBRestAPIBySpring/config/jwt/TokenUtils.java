package com.example.BoardDBRestAPIBySpring.config.jwt;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import io.jsonwebtoken.*;
import jakarta.xml.bind.DatatypeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenUtils {

    private static final String secretKey = JWTProperties.SECRET;

    public static String generateJwtToken(Member member){
        JwtBuilder builder = Jwts.builder()
                .setSubject(member.getMemberID())
                .setHeader(createHeader())
                .setClaims(createClaims(member))
                .setExpiration(createExpireDateForOneYear())
                .signWith(SignatureAlgorithm.HS256, createSigningKey());

        return builder.compact();
    }

    public static boolean isValidToken(String token){
        try{
            Claims claims=getClaimsFormToken(token);
            log.info("=============Token Utils==============");
            log.info("expireTime : "+claims.getExpiration());
            log.info("memberID : "+claims.get("memberID"));
            log.info("role : "+claims.get("roleID"));
            return true;
        }catch(ExpiredJwtException exception){
            log.error("Token Expired");
            return false;
        }catch (JwtException exception) {
            log.error("Token Tampered");
            return false;
        }catch (NullPointerException exception){
            log.error("Token is null");
            return false;
        }
    }

    public static String getTokenFromHeader(String header){
        return header.split(" ")[1];
    }


    private static Date createExpireDateForOneYear(){
        Calendar c=Calendar.getInstance();
        // Token 만료기간은 30일로 설정
        c.add(Calendar.DATE, 30);
        return c.getTime();
    }

    private static Map<String, Object> createHeader(){
        Map<String, Object> header=new HashMap<>();

        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());

        return header;
    }

    private static Map<String, Object> createClaims(Member member){
        // 공개 Claim에 사용자의 아이디와 이름을 설정하여 정보 조회
        Map<String, Object> claims = new HashMap<>();

        claims.put("memberID", member.getMemberID());
        claims.put("memberName", member.getMemberName());

        return claims;
    }

    private static Key createSigningKey(){
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private static Claims getClaimsFormToken(String token){
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getBody();
    }

    private static String getMemberIDFormToken(String token){
        Claims claims=getClaimsFormToken(token);
        return (String) claims.get("memberID");
    }

    private static Role getRoleFromToken(String token){
        Claims claims=getClaimsFormToken(token);
        return (Role) claims.get("roleID");
    }
}


