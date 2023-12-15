//package com.example.BoardDBRestAPIBySpring.domain;
//
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import org.springframework.data.redis.core.RedisHash;
//import org.springframework.data.redis.core.index.Indexed;
//
///*
//Redis로 RefreshToken 구현 : https://inkyu-yoon.github.io/docs/Language/SpringBoot/RefreshToken
// */
//@AllArgsConstructor
//@Data
//@RedisHash(value = "jwtToken", timeToLive = 60*60*24*3)
//public class RefreshToken {
//    @Id
//    private String memberID;
//    private String refreshToken;
//
//    // @Indexed 어노테이션이 있어야, 해당 필드 값으로 데이터를 찾아올 수 있다.
//    @Indexed
//    private String accessToken;
//}
