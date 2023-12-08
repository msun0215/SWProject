package com.example.BoardDBRestAPIBySpring.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import io.jsonwebtoken.*;
import jakarta.xml.bind.DatatypeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class TokenUtils {

    private static final String secretKey = JWTProperties.SECRET;

    public String generateJwtToken(Authentication authentication){

        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        String memberID=userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String accessToken= JWT.create().withSubject("ACCESSTOKEN")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+JWTProperties.EXPIRATION_TIME))
                .withClaim("memberID", memberID)
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(secretKey));

        return JWTProperties.TOKEN_PREFIX+accessToken;


//        Date now=new Date();
//        JwtBuilder builder = Jwts.builder()
//                .setSubject("ACCESSTOKEN")
//                .setHeader(createHeader())
//                .setIssuedAt(now)
//                .setClaims(createClaims(member))
//                .setExpiration(createExpireDateForOneYear())
//                .signWith(SignatureAlgorithm.HS256, createSigningKey());
//        String accessToken = builder.compact();

//        String refreshToken=Jwts.builder()
//                .setSubject(member.getMemberID())
//                .setHeader(createHeader())
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime()+))
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

    // Secret Key를 사용하여 Token Parsing
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


