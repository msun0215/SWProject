package com.example.BoardDBRestAPIBySpring.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDTO {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginDto{
        private String memberID;
        private String memberPW;

        @Builder
        public LoginDto(String memberID, String memberPW){
            this.memberID=memberID;
            this.memberPW=memberPW;
        }
    }



    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TokenDto{
        private String accessToken;
        private String refreshToken;

        public TokenDto(String accessToken, String refreshToken){
            this.accessToken=accessToken;
            this.refreshToken=refreshToken;
        }
    }
}
