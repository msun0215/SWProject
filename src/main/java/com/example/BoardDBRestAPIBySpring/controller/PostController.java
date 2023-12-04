package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import com.example.BoardDBRestAPIBySpring.response.PostResponse;
import com.example.BoardDBRestAPIBySpring.response.PostsResponse;
import com.example.BoardDBRestAPIBySpring.service.PostService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Slf4j
public class PostController {

	private final PostService postService;

	@GetMapping
	public PostsResponse getAllPosts(@PageableDefault Pageable pageable) {
		return postService.findAllPostsBy(pageable);
	}

	@GetMapping("/{id}")
	public PostResponse getPostById(@PathVariable Long id) {
		return postService.findById(id);
	}

	@PostMapping
	public ResponseEntity<Void> createBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
											@RequestBody PostCreateRequest request) {

		Member member = principalDetails.getMember();
		postService.createBoard(member, request);

		return ResponseEntity.created(URI.create("/boards")).build();
	}
}
