package com.example.BoardDBRestAPIBySpring.response;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PostsResponse {

	private final List<PostResponse> postResponses;
	private final int totalPages;
	private final long totalElements;
	private final int pageNumber;
	private final int pageSize;

	@Builder
	public PostsResponse(final List<PostResponse> postResponses, final int totalPages, final long totalElements,
						 final int pageNumber,
						 final int pageSize) {
		this.postResponses = postResponses;
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public static PostsResponse of(final Page<Board> boards) {
		List<PostResponse> postResponses = boards.getContent().stream()
				.map(PostResponse::new)
				.toList();
		return PostsResponse.builder()
				.postResponses(postResponses)
				.totalPages(boards.getTotalPages())
				.totalElements(boards.getTotalElements())
				.pageSize(boards.getSize())
				.pageNumber(boards.getNumber())
				.build();
	}
}
