//package com.example.BoardDBRestAPIBySpring.service;
//
//import com.example.BoardDBRestAPIBySpring.domain.RefreshToken;
//import com.example.BoardDBRestAPIBySpring.repository.RefreshTokenRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//// Controller에서 사용하기 위해 만든 클래스
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//    private final RefreshTokenRepository repository;
//
//    @Transactional
//    public void saveTokenInfo(String memberID, String refreshToken, String accessToken){
//        repository.save(new RefreshToken(memberID, refreshToken,accessToken));
//    }
//
//    @Transactional
//    public void removeRefreshToken(String accessToken){
//        repository.findByAccessToken(accessToken)
//                .ifPresent(refreshToken -> repository.delete(refreshToken));
//    }
//}
