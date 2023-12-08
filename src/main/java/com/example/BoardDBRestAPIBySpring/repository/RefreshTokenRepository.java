package com.example.BoardDBRestAPIBySpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByKeyEmail(String memberID);
    void deleteByKeyEmail(String memberID);
}
