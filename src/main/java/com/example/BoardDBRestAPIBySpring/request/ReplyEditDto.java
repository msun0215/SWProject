package com.example.BoardDBRestAPIBySpring.request;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReplyEditDto {

    private final long replyId;
    private final long boardId;
    private final Member member;
    private final ReplyEditRequest request;

    @Builder
    public ReplyEditDto(final long replyId, final long boardId, final Member member,
                        final ReplyEditRequest request) {
        this.replyId = replyId;
        this.boardId = boardId;
        this.member = member;
        this.request = request;
    }

    public String getContent() {
        return request.getContent();
    }
}
