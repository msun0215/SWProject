package com.example.BoardDBRestAPIBySpring.repository;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Data
@Table(name="T_REFRESH_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_TOKEN_ID", nullable = false)
    private Long refreshTokenId;

    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String refreshToken;

    @Column(name = "KEY_EMAIL", nullable = false)
    private String keyEmail;
}
