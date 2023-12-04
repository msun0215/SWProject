package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final PostRepository postRepository;

	public List<Board> findAllPosts() {
		return postRepository.findAll();
	}

	public List<Board> findAllPostsByPaging(Pageable pageable) {
		return postRepository.findAll(pageable).getContent();
	}

	public long calculatePostCounts() {
		return postRepository.count();
	}

	public Board save(final PostCreateRequest request) {
		return postRepository.save(request.toEntity());
	}
}
