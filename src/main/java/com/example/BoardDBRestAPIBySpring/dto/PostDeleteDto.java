package com.example.BoardDBRestAPIBySpring.dto;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDeleteDto {

    private final long boardId;
    private final Member member;

    @Builder
    public PostDeleteDto(final long boardId, final Member member) {
        this.boardId = boardId;
        this.member = member;
    }
}
