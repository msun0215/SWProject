package com.example.BoardDBRestAPIBySpring.response;

import com.example.BoardDBRestAPIBySpring.domain.Reply;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReplyResponse {

    private final Long id;
    private final String content;
    private final LocalDate createDate;
    private final String memberID;
    private final String memberName;
    private final long boardId;

    @Builder
    public ReplyResponse(final Long id, final String content, final LocalDate createDate, final String memberID,
                         final String memberName, final long boardId) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.memberID = memberID;
        this.memberName = memberName;
        this.boardId = boardId;
    }

    public static ReplyResponse of(final Reply reply) {
        return ReplyResponse.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .createDate(reply.getCreateDate())
                .memberID(reply.getMember().getMemberID())
                .memberName(reply.getMember().getMemberName())
                .boardId(reply.getBoard().getId())
                .build();
    }
}
