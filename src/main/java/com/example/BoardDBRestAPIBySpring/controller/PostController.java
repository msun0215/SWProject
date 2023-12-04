package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.response.PostResponse;
import com.example.BoardDBRestAPIBySpring.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Slf4j
public class PostController {

	private final PostService postService;

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
}
