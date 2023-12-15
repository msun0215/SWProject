//package com.example.BoardDBRestAPIBySpring.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//
///*
// * Redis의 연결을 정의하는 클래스이다.
// * RedisConnectionFactory를 통해 내장 혹은 외부의 Redis와 연결한다.
// * RedisTemplate를 통해 RedisConnection에서 넘겨준 byte 값을 객체 직렬화한다.
// */
//@RequiredArgsConstructor
//@Configuration
//@EnableRedisRepositories
//public class RedisRepositoryConfig {
//
//    private final RedisProperties redisProperties;
//
//    // lettuce
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
//    }
//
//    // redis-cli 사용을 위한 설정
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(){
//        RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
//}
