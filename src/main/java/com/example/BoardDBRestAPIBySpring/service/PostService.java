package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import com.example.BoardDBRestAPIBySpring.request.PostEditRequest;
import com.example.BoardDBRestAPIBySpring.response.PostResponse;
import com.example.BoardDBRestAPIBySpring.response.PostsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final PostRepository postRepository;

	@Transactional(readOnly = true)
	public PostsResponse findAllPostsBy(Pageable pageable) {
		Sort sort = Sort.by("id").descending();
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<Board> result = postRepository.findAll(pageRequest);
		return PostsResponse.of(result);
	}

	@Transactional(readOnly = true)
	public PostResponse findById(final Long id) {
		Board board = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		return new PostResponse(board);
	}

	public void createBoard(final Member member, final PostCreateRequest request) {
		Board board = request.toEntity();
		board.setMember(member);

		postRepository.save(board);
	}

	@Transactional
	public void editBoard(final Long boardId, final Member member, final PostEditRequest request) {
		Board board = postRepository.findById(boardId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		if (!board.isSameMember(member)) {
			throw new IllegalStateException("게시글 작성자가 아닙니다.");
		}
		board.edit(request);
	}

	@Transactional
	public void deleteBoard(final Long boardId, final Member member) {
		Board board = postRepository.findById(boardId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		if (!board.isSameMember(member)) {
			throw new IllegalStateException("게시글 작성자가 아닙니다.");
		}
		postRepository.delete(board);
	}
}

