package com.example.BoardDBRestAPIBySpring.response;

import com.example.BoardDBRestAPIBySpring.domain.Reply;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class RepliesResponse {

    private final List<ReplyResponse> repliesResponse;
    private final int totalPages;
    private final long totalElements;
    private final int pageNumber;
    private final int pageSize;

    @Builder
    public RepliesResponse(final List<ReplyResponse> repliesResponse, final int totalPages, final long totalElements,
                         final int pageNumber,
                         final int pageSize) {
        this.repliesResponse = repliesResponse;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static RepliesResponse of(final Page<Reply> replies) {
        List<ReplyResponse> postResponses = replies.getContent().stream()
                .map(ReplyResponse::of)
                .toList();
        return RepliesResponse.builder()
                .repliesResponse(postResponses)
                .totalPages(replies.getTotalPages())
                .totalElements(replies.getTotalElements())
                .pageSize(replies.getSize())
                .pageNumber(replies.getNumber())
                .build();
    }
}
