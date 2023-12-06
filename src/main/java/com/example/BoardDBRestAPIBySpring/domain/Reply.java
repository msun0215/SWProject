package com.example.BoardDBRestAPIBySpring.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.example.BoardDBRestAPIBySpring.request.ReplyEditDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDate createDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Builder
    public Reply(final String content, final LocalDate createDate) {
        this.content = content;
        this.createDate = createDate;
    }

    public static Reply of(final String content) {
        return Reply.builder()
                .content(content)
                .createDate(LocalDate.now())
                .build();
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    public void setBoard(final Board board) {
        this.board = board;
    }

    public boolean isNotSameBoardId(final long boardId) {
        return this.board.getId() != boardId;
    }

    public boolean isNotSameMember(final Member member) {
        return !this.member.isSame(member);
    }

    public void edit(final ReplyEditDto dto) {
        this.content = dto.getContent();
    }
}
