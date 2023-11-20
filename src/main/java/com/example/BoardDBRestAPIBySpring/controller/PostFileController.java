package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.service.PostFileService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Slf4j
public class PostFileController {

	private final PostFileService postFileService;

	@GetMapping("/postFile/{fileName}")
	public ResponseEntity<byte[]> downloadPostFile(@PathVariable final String fileName) throws IOException {
		byte[] postFile = postFileService.getFileByFileName(fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.attachment()
				.filename(fileName, StandardCharsets.UTF_8)
			.build());
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return ResponseEntity.status(HttpStatus.OK)
			.headers(headers)
			.body(postFile);
	}
}
