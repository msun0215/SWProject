package com.example.BoardDBRestAPIBySpring.request;

import com.example.BoardDBRestAPIBySpring.domain.Reply;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplyCreateRequest {

	@NotBlank(message = "내용은 필수입니다.")
	private final String content;

	@Builder
	public ReplyCreateRequest(final String content) {
		this.content = content;
	}

	public Reply toEntity() {
		return Reply.builder()
			.content(content)
			.createDate(LocalDate.now())
			.build();
	}
}
