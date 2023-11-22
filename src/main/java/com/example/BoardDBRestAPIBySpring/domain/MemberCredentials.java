package com.example.BoardDBRestAPIBySpring.domain;

import lombok.Data;

@Data
public class MemberCredentials {
    private String memberID;
    private String memberPW;

    // Getter, Setter 메소드
}