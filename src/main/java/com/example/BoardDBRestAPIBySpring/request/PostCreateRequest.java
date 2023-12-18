package com.example.BoardDBRestAPIBySpring.request;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostCreateRequest {

	@NotBlank(message = "제목은 필수입니다.")
	@PostTitle
	private final String title;

	@NotBlank(message = "내용은 필수입니다.")
	private final String content;

	@Builder
	public PostCreateRequest(final String title, final String content) {
		this.title = title;
		this.content = content;
	}

	public Board toEntity() {
		return Board.builder()
			.title(title)
			.content(content)
			.createDate(LocalDate.now())
			.modifyDate(LocalDate.now())
			.build();
	}
}
