package com.example.BoardDBRestAPIBySpring.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;

    @Builder
    public ErrorResponse(final String message) {
        this.message = message;
    }
}
