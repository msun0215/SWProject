package com.example.BoardDBRestAPIBySpring.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.domain.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Slf4j
//@RequiredArgsConstructor
@Component
public class TokenUtils {

    private final String secretKey=JWTProperties.SECRET;
    private final Key key;
    public TokenUtils(@Value("${jwt.secret}") String secretKey){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        this.key= Keys.hmacShaKeyFor(keyBytes);
    }

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

    }

    public String generateRefreshToken(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String memberID = userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String refreshToken = JWT.create().withSubject("REFRESHTOKEN")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWTProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("memberID", memberID)
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(secretKey));

        return JWTProperties.TOKEN_PREFIX + refreshToken;
    }

    public Token createToken(Authentication authentication){
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        String memberID = userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Claims claims= Jwts.claims().setSubject(memberID);  // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles);     // 정보는 key/value 쌍으로 저장
        Date now=new Date();

        // AccessToken
        String accessToken=Jwts.builder()
                .setClaims(claims)  // 정보 저장
                .setIssuedAt(now)   // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime()+JWTProperties.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, key)  // 사용할 알고리즘과 secretKey
                .compact();

        // RefreshToken
        String refreshToken=Jwts.builder()
                .setClaims(claims)  // 정보 저장
                .setIssuedAt(now)   // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime()+JWTProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256,key)   // 사용할 알고리즘과 secretKey
                .compact();

        System.out.println("TokenUtils에서의 accessToken : "+accessToken);
        System.out.println("TokenUtils에서의 refreshToken : "+refreshToken);


        return Token.builder().grantType(JWTProperties.TOKEN_PREFIX).accessToken(accessToken).refreshToken(refreshToken).key(memberID).build();
    }

//    // JWT token을 복호화하여 Token에 들어있는 정보를 꺼내는 메소드=>JWTAuthorizationFIlter.doFilterInternal
//    public Authentication getAuthentication(String accessToken){
//        // Token Decoding
//        Claims claims=parseClaims(accessToken);
//
//        if(claims.get(JWTProperties.HEADER_STRING)==null)
//            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
//
//        // Claims에서 권한 정보 가져오기
//        Collection<? extends  GrantedAuthority> authorities=
//                Arrays.stream(claims.get(JWTProperties.HEADER_STRING).toString().split(","))
//                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//
//        // UserDetails 객체를 만들어서 Authentication return
//        //PrincipalDetails principalDetails=new Member(claims.getSubject(),"",authorities);
//        UserDetails userDetails= new User(claims.getSubject(),"",authorities);
//        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
//    }

    private Claims parseClaims(String accessToken){
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }



    public static boolean isValidToken(String token){
        try{
            Jwts.parser().setSigningKey(token).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }





    /*

    public static String getTokenFromHeader(String header){
        return header.split(" ")[1];
    }


 // Secret Key를 사용하여 Token Parsing
    private static Claims getClaimsFormToken(String token){
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getBody();
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



    private static String getMemberIDFormToken(String token){
        Claims claims=getClaimsFormToken(token);
        return (String) claims.get("memberID");
    }

    private static Role getRoleFromToken(String token){
        Claims claims=getClaimsFormToken(token);
        return (Role) claims.get("roleID");
    }

     */
}

