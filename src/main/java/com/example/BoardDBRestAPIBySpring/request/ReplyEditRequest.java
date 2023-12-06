package com.example.BoardDBRestAPIBySpring.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplyEditRequest {

	@NotBlank(message = "내용은 필수입니다.")
	private String content;

	private ReplyEditRequest() {
	}

	@Builder
	public ReplyEditRequest(final String content) {
		this.content = content;
	}
}
