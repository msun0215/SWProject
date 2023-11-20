package com.example.BoardDBRestAPIBySpring.response;

public class PostFileResponse {

	private final byte[] file;

	public PostFileResponse(final byte[] file) {
		this.file = file;
	}
}
