package com.example.BoardDBRestAPIBySpring.domain;

import lombok.Data;

@Data
public class LoginRequest {
    private String memberID;
    private String memberPW;
}
