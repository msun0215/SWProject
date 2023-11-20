package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import com.example.BoardDBRestAPIBySpring.response.PostResponse;
import com.example.BoardDBRestAPIBySpring.service.PostFileService;
import com.example.BoardDBRestAPIBySpring.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Slf4j
public class PostController {

	private final PostService postService;
	private final PostFileService postFileService;

	@GetMapping
	public List<PostResponse> getAllPosts() {
		List<Board> posts = postService.findAllPosts();
		return posts.stream()
			.map(PostResponse::new)
			.toList();
	}

	@GetMapping("/paging")
	public List<PostResponse> getAllPostsByPaging(Pageable pageable) {
		List<Board> postsByPaging = postService.findAllPostsByPaging(pageable);
		return postsByPaging.stream()
			.map(PostResponse::new)
			.toList();
	}

	@GetMapping("/counts")
	public long getPostCounts() {
		return postService.calculatePostCounts();
	}

	// 참조
	// https://velog.io/@sunclock/Postman%EC%97%90%EC%84%9C-file%EA%B3%BC-json-body-%ED%95%9C%EB%B2%88%EC%97%90-POST-%EC%9A%94%EC%B2%AD%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
		MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public void create(@ModelAttribute @Valid PostCreateRequest request) {
		log.info("postCreateRequest = {}", request);
		log.info("postCreateRequest.files size = {}", request.getFiles().size());

		Board board = postService.save(request);
		postFileService.save(board, request.getFiles());
	}
}
