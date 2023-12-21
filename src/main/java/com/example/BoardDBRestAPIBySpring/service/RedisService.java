package com.example.BoardDBRestAPIBySpring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;


    // {key, value} 값을 저장한다
    @Transactional
    public void setValues(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }


    // {key, value} 값을 유효시간(timeout)과 함께 저장한다
    // 만료시간 설정 -> 자동 삭제
    @Transactional
    public void setValuesWithTimeout(String key, String value, Long timeout){
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    // key값을 이용해 value값을 가져온다
    public String getValues(String key){
        return redisTemplate.opsForValue().get(key);
    }

    // key값을 이용해 데이터를 삭제한다
    @Transactional
    public void deleteValues(String key){
        redisTemplate.delete(key);
    }


    @Transactional
    public boolean checkExistsValue(String value, String key){
        System.out.println("check key : "+key);
        System.out.println("check value : "+value);
        return redisTemplate.opsForValue().get(key).equals(value);
    }
}
