package com.example.BoardDBRestAPIBySpring.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostEditRequest {

	@NotBlank(message = "제목은 필수입니다.")
	@PostTitle
	private final String title;

	@NotBlank(message = "내용은 필수입니다.")
	private final String content;

	@Builder
	public PostEditRequest(final String title, final String content) {
		this.title = title;
		this.content = content;
	}
}
