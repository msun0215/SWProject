package com.example.BoardDBRestAPIBySpring.dto;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.request.PostEditRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEditDto {

    private final long boardId;
    private final Member member;
    private final PostEditRequest request;

    @Builder
    public PostEditDto(final long boardId, final Member member, final PostEditRequest request) {
        this.boardId = boardId;
        this.member = member;
        this.request = request;
    }

    public String getContent() {
        return request.getContent();
    }
}
