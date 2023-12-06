package com.example.BoardDBRestAPIBySpring.dto;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReplyDeletetDto {

    private final long replyId;
    private final long boardId;
    private final Member member;

    @Builder
    public ReplyDeletetDto(final long replyId, final long boardId, final Member member) {
        this.replyId = replyId;
        this.boardId = boardId;
        this.member = member;
    }
}
