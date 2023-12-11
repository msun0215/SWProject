package com.example.BoardDBRestAPIBySpring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String grantType;   // 인증 타입(Bearer)
    private String accessToken;
    private String refreshToken;
    private String key;
}
