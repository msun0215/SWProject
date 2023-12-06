package com.example.BoardDBRestAPIBySpring.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostRoleChangeRequest {


	@NotBlank(message = "바꿀 권한 이름은 필수입니다.")
	private String changeRole;

	private PostRoleChangeRequest() {
	}

	@Builder
	public PostRoleChangeRequest(final String changeRole) {
		this.changeRole = changeRole;
	}
}
